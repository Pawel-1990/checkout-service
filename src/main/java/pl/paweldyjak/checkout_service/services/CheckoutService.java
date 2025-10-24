package pl.paweldyjak.checkout_service.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.CheckoutItem;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutItemDetails;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.entities.Checkout;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;
import pl.paweldyjak.checkout_service.exceptions.checkout_exceptions.*;
import pl.paweldyjak.checkout_service.mappers.CheckoutMapper;
import pl.paweldyjak.checkout_service.repositories.CheckoutRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final ItemService itemService;
    private final CheckoutMapper checkoutMapper;
    private final BundleDiscountService bundleDiscountService;

    public CheckoutService(CheckoutRepository checkoutRepository, ItemService itemService, CheckoutMapper checkoutMapper, BundleDiscountService bundleDiscountService) {
        this.checkoutRepository = checkoutRepository;
        this.itemService = itemService;
        this.checkoutMapper = checkoutMapper;
        this.bundleDiscountService = bundleDiscountService;
    }

    @Transactional(readOnly = true)
    public List<CheckoutResponse> getAllCheckouts() {
        return checkoutRepository.findAll().stream()
                .map(checkoutMapper::mapToCheckoutResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CheckoutResponse getCheckoutById(Long id) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));
        return checkoutMapper.mapToCheckoutResponse(checkout);
    }

    public CheckoutResponse createCheckout() {
        Checkout checkout = Checkout.create();
        Checkout savedCheckout = checkoutRepository.save(checkout);
        return checkoutMapper.mapToCheckoutResponse(savedCheckout);
    }

    public CheckoutResponse updateCheckoutItemsAndPrices(Long checkoutId, List<CheckoutItem> newItems) {
        Checkout existingCheckout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException(checkoutId));

        List<String> itemNamesToAdd = newItems.stream().map(CheckoutItem::itemName).toList();
        List<Item> itemEntities = itemService.findAllItemsByNameIn(itemNamesToAdd);

        if (itemNamesToAdd.size() != itemEntities.size()) {
            throw new ItemUnavailableException(
                    itemNamesToAdd.stream()
                            .filter(itemName -> !itemEntities.stream()
                                    .map(Item::getName).toList().contains(itemName)).findFirst().orElse(""));
        }

        existingCheckout = updateCheckoutItems(newItems, existingCheckout);

        List<CheckoutItemDetails> checkoutItemDetails = generateCheckoutItemDetails(existingCheckout, itemEntities);

        existingCheckout = updateCheckoutPrices(existingCheckout, checkoutItemDetails);

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout);
    }

    private Checkout updateCheckoutItems(List<CheckoutItem> newItems, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (CheckoutItem item : newItems) {
            String itemName = item.itemName();
            int quantity = item.quantity();
            items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        }
        checkout.setItems(items);
        return checkout;
    }

    public CheckoutResponse deleteItemsFromCheckout(Long id, List<CheckoutItem> items) {
        Checkout existingCheckout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));

        List<String> itemNamesToDelete = items.stream().map(CheckoutItem::itemName).toList();
        List<String> itemNamesInCheckout = existingCheckout.getItems().keySet().stream().toList();
        List<Item> itemEntities = itemService.findAllItemsByNameIn(itemNamesToDelete);

        if (!itemNamesInCheckout.containsAll(itemNamesToDelete)) {
            throw new ItemNotFoundInCheckout(itemNamesToDelete.stream().filter(itemName -> !itemNamesInCheckout.contains(itemName)).findFirst().orElse(""));
        }

        existingCheckout = deleteItemsFromCheckout(items, existingCheckout);

        List<CheckoutItemDetails> checkoutItemDetails = generateCheckoutItemDetails(existingCheckout, itemEntities);

        if (!checkoutItemDetails.isEmpty()) {
            existingCheckout = updateCheckoutPrices(existingCheckout, checkoutItemDetails);
        } else {
            existingCheckout.setFinalPrice(BigDecimal.ZERO);
            existingCheckout.setTotalDiscount(BigDecimal.ZERO);
            existingCheckout.setPriceBeforeDiscount(BigDecimal.ZERO);
        }

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout);
    }

    public Checkout deleteItemsFromCheckout(List<CheckoutItem> itemsToRemove, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (CheckoutItem item : itemsToRemove) {
            String itemName = item.itemName();
            int quantity = item.quantity();
            if (quantity <= 0) {
                continue;
            }

            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                if (itemName.equals(entry.getKey())) {
                    if (entry.getValue() < quantity) {
                        throw new InaccurateQuantityToDeleteException(itemName, quantity, entry.getValue());
                    }
                    entry.setValue(entry.getValue() - quantity);
                    if (entry.getValue() == 0) {
                        items.remove(itemName);
                    }
                    break;
                }
            }
        }
        checkout.setItems(items);
        return checkout;
    }

    public void deleteCheckout(Long id) {
        if (!checkoutRepository.existsById(id)) {
            throw new CheckoutNotFoundException(id);
        }
        checkoutRepository.deleteById(id);
    }

    public List<CheckoutItemDetails> generateCheckoutItemDetails(Checkout checkout, List<Item> itemEntities) {
        if (checkout == null) {
            return null;
        }
        List<CheckoutItemDetails> checkoutItemDetailsList = new ArrayList<>();
        BigDecimal priceBeforeDiscount;
        BigDecimal finalPrice;
        BigDecimal discount;

        for (Map.Entry<String, Integer> checkoutItemEntry : checkout.getItems().entrySet()) {
            String itemName = checkoutItemEntry.getKey();
            long itemQuantity = Long.valueOf(checkoutItemEntry.getValue());

            for (Item item : itemEntities) {
                if (itemName.equals(item.getName())) {
                    int requiredQuantity = item.getRequiredQuantity();
                    BigDecimal normalPriceForItem = item.getNormalPrice();
                    priceBeforeDiscount = normalPriceForItem.multiply(BigDecimal.valueOf(itemQuantity));

                    if (itemQuantity >= requiredQuantity) {
                        finalPrice = item.getSpecialPrice().multiply(BigDecimal.valueOf(itemQuantity));
                        discount = priceBeforeDiscount.subtract(finalPrice);
                    } else {
                        finalPrice = normalPriceForItem.multiply(BigDecimal.valueOf(itemQuantity));
                        discount = BigDecimal.ZERO;
                    }
                    CheckoutItemDetails checkoutItemDetails = CheckoutItemDetails.builder()
                            .itemName(itemName)
                            .quantity((int) itemQuantity)
                            .discountedQuantity(requiredQuantity)
                            .unitPrice(item.getNormalPrice())
                            .quantityDiscountApplies((int) itemQuantity >= requiredQuantity)
                            .priceBeforeDiscount(priceBeforeDiscount)
                            .discountAmount(discount)
                            .priceAfterDiscount(finalPrice)
                            .build();
                    checkoutItemDetailsList.add(checkoutItemDetails);
                    break;
                }
            }
        }
        return checkoutItemDetailsList;
    }

    public Checkout updateCheckoutPrices(Checkout checkout, List<CheckoutItemDetails> checkoutItemDetailsList) {
        if (checkout.getItems().isEmpty() || checkoutItemDetailsList.isEmpty()) {
            return checkout;
        }

        BigDecimal priceBeforeDiscount = new BigDecimal(0);
        BigDecimal quantityDiscount = new BigDecimal(0);
        BigDecimal finalPrice = new BigDecimal(0);
        BigDecimal bundleDiscount;
        BigDecimal totalDiscount;

        for (CheckoutItemDetails checkoutItemDetails : checkoutItemDetailsList) {
            priceBeforeDiscount = priceBeforeDiscount.add(checkoutItemDetails.priceBeforeDiscount());
            quantityDiscount = quantityDiscount.add(checkoutItemDetails.discountAmount());
            finalPrice = finalPrice.add(checkoutItemDetails.priceAfterDiscount());
        }

        bundleDiscount = bundleDiscountService.getSumDiscountsForItemNames(checkout.getItems().keySet().stream().toList());

        if (bundleDiscount.compareTo(BigDecimal.ZERO) > 0) {
            totalDiscount = quantityDiscount.add(bundleDiscount);
            finalPrice = priceBeforeDiscount.subtract(totalDiscount);
        } else {
            bundleDiscount = BigDecimal.ZERO;
        }

        checkout.setPriceBeforeDiscount(priceBeforeDiscount);
        checkout.setQuantityDiscount(quantityDiscount);
        checkout.setBundleDiscount(bundleDiscount);
        checkout.setTotalDiscount(quantityDiscount);
        checkout.setFinalPrice(finalPrice);

        return checkout;
    }

    public ReceiptResponse pay(Long id) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));
        List<String> itemNames = checkout.getItems().keySet().stream().toList();
        List<Item> itemEntities = itemService.findAllItemsByNameIn(itemNames);

        List<CheckoutItemDetails> checkoutItemDetailsList = generateCheckoutItemDetails(checkout, itemEntities);

        if (checkoutItemDetailsList.isEmpty()) {
            throw new EmptyCheckoutException();
        }
        checkout.setStatus(CheckoutStatus.PAID);
        checkout.setReceipt(checkoutMapper.mapToReceiptResponse(checkout, checkoutItemDetailsList));
        Checkout updatedCheckout = checkoutRepository.save(checkout);
        return updatedCheckout.getReceipt();
    }

    public ReceiptResponse getReceiptByCheckoutId(Long id) {
        Optional<Checkout> checkout = Optional.ofNullable(checkoutRepository.findById(id).orElseThrow(() -> new CheckoutNotFoundException(id)));
        return checkout.map(Checkout::getReceipt).orElseThrow(() -> new ReceiptNotFoundException(id));
    }
}
