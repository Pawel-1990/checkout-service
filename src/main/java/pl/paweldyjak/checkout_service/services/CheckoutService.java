package pl.paweldyjak.checkout_service.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemDto;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponseDto;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutItemDetailsDto;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponseDto;
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
    public List<CheckoutResponseDto> getAllCheckouts() {
        return checkoutRepository.findAll().stream()
                .map(checkoutMapper::mapToCheckoutResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CheckoutResponseDto getCheckoutById(Long id) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));
        return checkoutMapper.mapToCheckoutResponse(checkout);
    }

    public ReceiptResponseDto getReceiptByCheckoutId(Long id) {
        Optional<Checkout> checkout = Optional.ofNullable(checkoutRepository.findById(id).orElseThrow(() -> new CheckoutNotFoundException(id)));
        return checkout.map(Checkout::getReceipt).orElseThrow(() -> new ReceiptNotFoundException(id));
    }

    public CheckoutResponseDto createCheckout() {
        Checkout checkout = Checkout.create();
        Checkout savedCheckout = checkoutRepository.save(checkout);
        return checkoutMapper.mapToCheckoutResponse(savedCheckout);
    }

    public void deleteCheckout(Long id) {
        if (!checkoutRepository.existsById(id)) {
            throw new CheckoutNotFoundException(id);
        }
        checkoutRepository.deleteById(id);
    }

    public CheckoutResponseDto updateCheckoutItemsAndPricesForAdding(Long checkoutId, List<CheckoutItemDto> newItems) {
        Checkout existingCheckout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException(checkoutId));

        List<String> itemNamesToAdd = newItems.stream().map(CheckoutItemDto::itemName).toList();
        List<Item> itemEntities = itemService.findAllItemsByNameIn(itemNamesToAdd);

        if (itemNamesToAdd.size() != itemEntities.size()) {
            throw new ItemUnavailableException(
                    itemNamesToAdd.stream()
                            .filter(itemName -> !itemEntities.stream()
                                    .map(Item::getName).toList().contains(itemName)).findFirst().orElse(""));
        }

        existingCheckout = updateCheckoutItemsForAdding(newItems, existingCheckout);

        List<CheckoutItemDetailsDto> checkoutItemDetailDtos = generateCheckoutItemDetails(existingCheckout, itemEntities);

        existingCheckout = updateCheckoutPrices(existingCheckout, checkoutItemDetailDtos);

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout);
    }

    private Checkout updateCheckoutItemsForAdding(List<CheckoutItemDto> newItems, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (CheckoutItemDto item : newItems) {
            String itemName = item.itemName();
            int quantity = item.quantity();
            items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        }
        checkout.setItems(items);
        return checkout;
    }

    public CheckoutResponseDto updateCheckoutItemsAndPricesForDeleting(Long id, List<CheckoutItemDto> items) {
        Checkout existingCheckout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));

        List<String> itemNamesToDelete = items.stream().map(CheckoutItemDto::itemName).toList();
        List<String> itemNamesInCheckout = existingCheckout.getItems().keySet().stream().toList();

        if (!itemNamesInCheckout.containsAll(itemNamesToDelete)) {
            throw new ItemNotFoundInCheckout(itemNamesToDelete.stream().filter(itemName -> !itemNamesInCheckout.contains(itemName)).findFirst().orElse(""));
        }

        existingCheckout = updateCheckoutItemsForDeleting(items, existingCheckout);

        // getting item entities is necessary to calculate a discount for remaining items in checkout
        List<Item> itemEntities = itemService.findAllItemsByNameIn(existingCheckout.getItems().keySet().stream().toList());

        List<CheckoutItemDetailsDto> checkoutItemDetailDtos = generateCheckoutItemDetails(existingCheckout, itemEntities);

        existingCheckout = updateCheckoutPrices(existingCheckout, checkoutItemDetailDtos);

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout);
    }

    public Checkout updateCheckoutItemsForDeleting(List<CheckoutItemDto> itemsToRemove, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (CheckoutItemDto item : itemsToRemove) {
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

    public List<CheckoutItemDetailsDto> generateCheckoutItemDetails(Checkout checkout, List<Item> itemEntities) {
        if (checkout == null) {
            return null;
        }
        List<CheckoutItemDetailsDto> checkoutItemDetailsDtoList = new ArrayList<>();
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
                    CheckoutItemDetailsDto checkoutItemDetailsDto = CheckoutItemDetailsDto.builder()
                            .itemName(itemName)
                            .quantity((int) itemQuantity)
                            .discountedQuantity(requiredQuantity)
                            .unitPrice(item.getNormalPrice())
                            .quantityDiscountApplies((int) itemQuantity >= requiredQuantity)
                            .priceBeforeDiscount(priceBeforeDiscount)
                            .discountAmount(discount)
                            .priceAfterDiscount(finalPrice)
                            .build();
                    checkoutItemDetailsDtoList.add(checkoutItemDetailsDto);
                    break;
                }
            }
        }
        return checkoutItemDetailsDtoList;
    }

    public Checkout updateCheckoutPrices(Checkout checkout, List<CheckoutItemDetailsDto> checkoutItemDetailsDtoList) {
        if (checkout.getItems().isEmpty() || checkoutItemDetailsDtoList.isEmpty()) {
            return checkout;
        }

        BigDecimal priceBeforeDiscount = new BigDecimal(0);
        BigDecimal quantityDiscount = new BigDecimal(0);
        BigDecimal finalPrice = new BigDecimal(0);
        BigDecimal bundleDiscount;
        BigDecimal totalDiscount;

        for (CheckoutItemDetailsDto checkoutItemDetailsDto : checkoutItemDetailsDtoList) {
            priceBeforeDiscount = priceBeforeDiscount.add(checkoutItemDetailsDto.priceBeforeDiscount());
            quantityDiscount = quantityDiscount.add(checkoutItemDetailsDto.discountAmount());
            finalPrice = finalPrice.add(checkoutItemDetailsDto.priceAfterDiscount());
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
        checkout.setTotalDiscount(quantityDiscount.add(bundleDiscount));
        checkout.setFinalPrice(finalPrice);

        return checkout;
    }

    public ReceiptResponseDto pay(Long id) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));
        List<String> itemNames = checkout.getItems().keySet().stream().toList();
        List<Item> itemEntities = itemService.findAllItemsByNameIn(itemNames);

        List<CheckoutItemDetailsDto> checkoutItemDetailsDtoList = generateCheckoutItemDetails(checkout, itemEntities);

        if (checkoutItemDetailsDtoList.isEmpty()) {
            throw new EmptyCheckoutException();
        }
        checkout.setStatus(CheckoutStatus.PAID);
        checkout.setReceipt(checkoutMapper.mapToReceiptResponse(checkout, checkoutItemDetailsDtoList));
        Checkout updatedCheckout = checkoutRepository.save(checkout);
        return updatedCheckout.getReceipt();
    }
}
