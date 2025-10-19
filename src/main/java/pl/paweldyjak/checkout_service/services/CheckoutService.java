package pl.paweldyjak.checkout_service.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.request.ItemsToModifyRequest;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptItemDetails;
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

@Service
@Transactional
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final ItemService itemService;
    private final CheckoutMapper checkoutMapper;

    public CheckoutService(CheckoutRepository checkoutRepository, ItemService itemService, CheckoutMapper checkoutMapper) {
        this.checkoutRepository = checkoutRepository;
        this.itemService = itemService;
        this.checkoutMapper = checkoutMapper;
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

    public CheckoutResponse addItemsToCheckout(Long checkoutId, List<ItemsToModifyRequest> items) {
        Checkout existingCheckout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException(checkoutId));

        areItemsAvailable(items.stream().map(ItemsToModifyRequest::itemName).toList());

        existingCheckout = addItemsToCheckout(items, existingCheckout);

        List<ReceiptItemDetails> receiptItemDetails = getDetailedItemsListFromCheckoutItems(existingCheckout);

        existingCheckout = updatePrices(existingCheckout, receiptItemDetails);

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout);
    }

    private Checkout addItemsToCheckout(List<ItemsToModifyRequest> newItems, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (ItemsToModifyRequest item : newItems) {
            String itemName = item.itemName();
            int quantity = item.quantity();
            items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        }
        checkout.setItems(items);
        return checkout;
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

    public CheckoutResponse deleteItemsFromCheckout(Long id, List<ItemsToModifyRequest> items) {
        Checkout existingCheckout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));

        List<String> itemNamesToDelete = items.stream().map(ItemsToModifyRequest::itemName).toList();
        List<String> itemNamesInCheckout = existingCheckout.getItems().keySet().stream().toList();

        // check if all items are available in checkout
        if (!itemNamesInCheckout.containsAll(itemNamesToDelete)) {
            throw new ItemNotFoundInCheckout(itemNamesToDelete.stream().filter(itemName -> !itemNamesInCheckout.contains(itemName)).findFirst().orElse(""));
        }

        existingCheckout = deleteItemsFromCheckout(items, existingCheckout);

        // get item details
        List<ReceiptItemDetails> receiptItemDetails = getDetailedItemsListFromCheckoutItems(existingCheckout);

        if (!receiptItemDetails.isEmpty()) {
            existingCheckout = updatePrices(existingCheckout, receiptItemDetails);
        } else {
            existingCheckout.setFinalPrice(BigDecimal.ZERO);
            existingCheckout.setTotalDiscount(BigDecimal.ZERO);
            existingCheckout.setPriceBeforeDiscount(BigDecimal.ZERO);
        }

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout);
    }

    public Checkout deleteItemsFromCheckout(List<ItemsToModifyRequest> itemsToRemove, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (ItemsToModifyRequest item : itemsToRemove) {
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

    public List<ReceiptItemDetails> getDetailedItemsListFromCheckoutItems(Checkout checkout) {
        if (checkout == null) {
            return null;
        }
        List<ReceiptItemDetails> receiptItemDetailsList = new ArrayList<>();
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
                    ReceiptItemDetails receiptItemDetails = ReceiptItemDetails.builder()
                            .itemName(itemName)
                            .quantity((int) itemQuantity)
                            .discountedQuantity(requiredQuantity)
                            .unitPrice(item.getNormalPrice())
                            .discountApplies((int) itemQuantity >= requiredQuantity)
                            .priceBeforeDiscount(priceBeforeDiscount)
                            .discountAmount(discount)
                            .priceAfterDiscount(finalPrice)
                            .build();
                    receiptItemDetailsList.add(receiptItemDetails);
                    break;
                }
            }
        }
        return receiptItemDetailsList;
    }

    public Checkout updatePrices(Checkout checkout, List<ReceiptItemDetails> receiptItemDetailsList) {
        if (checkout.getItems().isEmpty() || receiptItemDetailsList.isEmpty()) {
            return checkout;
        }

        BigDecimal priceBeforeDiscount = new BigDecimal(0);
        BigDecimal totalDiscount = new BigDecimal(0);
        BigDecimal finalPrice = new BigDecimal(0);

        for (ReceiptItemDetails receiptItemDetails : receiptItemDetailsList) {
            priceBeforeDiscount = priceBeforeDiscount.add(receiptItemDetails.priceBeforeDiscount());
            totalDiscount = totalDiscount.add(receiptItemDetails.discountAmount());
            finalPrice = finalPrice.add(receiptItemDetails.priceAfterDiscount());
        }

        checkout.setPriceBeforeDiscount(priceBeforeDiscount);
        checkout.setTotalDiscount(totalDiscount);
        checkout.setFinalPrice(finalPrice);
        return checkout;
    }

    public ReceiptResponse pay(Long id) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));

        List<ReceiptItemDetails> receiptItemDetailsList = getDetailedItemsListFromCheckoutItems(checkout);

        if (receiptItemDetailsList.isEmpty()) {
            throw new EmptyCheckoutException();
        }
        checkout.setStatus(CheckoutStatus.PAID);
        checkout.setReceipt(checkoutMapper.mapToReceiptResponse(checkout, receiptItemDetailsList));
        Checkout updatedCheckout = checkoutRepository.save(checkout);
        return updatedCheckout.getReceipt();
    }
}
