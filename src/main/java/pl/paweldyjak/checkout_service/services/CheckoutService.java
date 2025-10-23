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

    public CheckoutResponse addItemsToCheckout(Long checkoutId, List<CheckoutItem> items) {
        Checkout existingCheckout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException(checkoutId));

        areItemsAvailable(items.stream().map(CheckoutItem::itemName).toList());

        addItemsToCheckout(items, existingCheckout);

        List<CheckoutItemDetails> checkoutItemDetails = getDetailedItemsListFromCheckoutItems(existingCheckout);

        existingCheckout = updatePrices(existingCheckout, checkoutItemDetails);

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout);
    }

    private void addItemsToCheckout(List<CheckoutItem> newItems, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (CheckoutItem item : newItems) {
            String itemName = item.itemName();
            int quantity = item.quantity();
            items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        }
        checkout.setItems(items);
    }
    @Transactional(readOnly = true)
    public void areItemsAvailable(List<String> itemNames) {

        List<String> allAvailableItemNames = itemService.getAllAvailableItemNames();
        for (String itemName : itemNames) {
            if (!allAvailableItemNames.contains(itemName)) {
                throw new ItemUnavailableException(itemName);
            }
        }
    }

    public CheckoutResponse deleteItemsFromCheckout(Long id, List<CheckoutItem> items) {
        Checkout existingCheckout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));

        List<String> itemNamesToDelete = items.stream().map(CheckoutItem::itemName).toList();
        List<String> itemNamesInCheckout = existingCheckout.getItems().keySet().stream().toList();

        if (!itemNamesInCheckout.containsAll(itemNamesToDelete)) {
            throw new ItemNotFoundInCheckout(itemNamesToDelete.stream().filter(itemName -> !itemNamesInCheckout.contains(itemName)).findFirst().orElse(""));
        }

        existingCheckout = deleteItemsFromCheckout(items, existingCheckout);

        // get item details
        List<CheckoutItemDetails> checkoutItemDetails = getDetailedItemsListFromCheckoutItems(existingCheckout);

        if (!checkoutItemDetails.isEmpty()) {
            existingCheckout = updatePrices(existingCheckout, checkoutItemDetails);
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

    public List<CheckoutItemDetails> getDetailedItemsListFromCheckoutItems(Checkout checkout) {
        if (checkout == null) {
            return null;
        }
        List<CheckoutItemDetails> checkoutItemDetailsList = new ArrayList<>();
        List<Item> availableItems = itemService.getAllItemsEntities();
        BigDecimal priceBeforeDiscount;
        BigDecimal finalPrice;
        BigDecimal discount;

        for (Map.Entry<String, Integer> entry : checkout.getItems().entrySet()) {
            String itemName = entry.getKey();
            long itemQuantity = Long.valueOf(entry.getValue());

            for (Item item : availableItems) {
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

    public Checkout updatePrices(Checkout checkout, List<CheckoutItemDetails> checkoutItemDetailsList) {
        if (checkout.getItems().isEmpty() || checkoutItemDetailsList.isEmpty()) {
            return checkout;
        }

        BigDecimal priceBeforeDiscount = new BigDecimal(0);
        BigDecimal quantityDiscount = new BigDecimal(0);
        BigDecimal finalPrice = new BigDecimal(0);
        BigDecimal bundleDiscountAmount = new BigDecimal(0);

        for (CheckoutItemDetails checkoutItemDetails : checkoutItemDetailsList) {
            priceBeforeDiscount = priceBeforeDiscount.add(checkoutItemDetails.priceBeforeDiscount());
            quantityDiscount = quantityDiscount.add(checkoutItemDetails.discountAmount());
            finalPrice = finalPrice.add(checkoutItemDetails.priceAfterDiscount());
        }

        checkout.setPriceBeforeDiscount(priceBeforeDiscount);
        checkout.setQuantityDiscount(quantityDiscount);
        checkout.setFinalPrice(finalPrice);
        checkout.setTotalDiscount(quantityDiscount);

        checkout = applyBundleDiscounts(checkout);
        return checkout;
    }

    public Checkout applyBundleDiscounts(Checkout checkout) {

        // reset bundle discount and total discount and recalculate them
        checkout.setBundleDiscount(BigDecimal.ZERO);
        if (checkout.getQuantityDiscount().compareTo(BigDecimal.ZERO) == 0) {
            checkout.setTotalDiscount(BigDecimal.ZERO);
        }

        List<String> listOfNames = checkout.getItems().keySet().stream().toList();
        BigDecimal priceBundleDiscount = bundleDiscountService.getSumDiscountsForItemNames(listOfNames);
        if (priceBundleDiscount.compareTo(BigDecimal.ZERO) > 0) {
            checkout.setBundleDiscount(priceBundleDiscount);
            checkout.setTotalDiscount(checkout.getQuantityDiscount().add(priceBundleDiscount));
            checkout.setFinalPrice(checkout.getPriceBeforeDiscount().subtract(checkout.getTotalDiscount()));
        }
        return checkout;
    }

    public ReceiptResponse pay(Long id) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));

        List<CheckoutItemDetails> checkoutItemDetailsList = getDetailedItemsListFromCheckoutItems(checkout);

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
