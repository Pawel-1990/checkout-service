package pl.paweldyjak.checkout_service.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.request.AddItemsRequest;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ItemDetails;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.entities.Checkout;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.checkout_exceptions.CheckoutNotFoundException;
import pl.paweldyjak.checkout_service.exceptions.checkout_exceptions.InaccurateQuantityToDeleteException;
import pl.paweldyjak.checkout_service.exceptions.checkout_exceptions.ItemNotFoundInCheckout;
import pl.paweldyjak.checkout_service.exceptions.checkout_exceptions.ItemUnavailableException;
import pl.paweldyjak.checkout_service.mappers.CheckoutMapper;
import pl.paweldyjak.checkout_service.repositories.CheckoutRepository;
import pl.paweldyjak.checkout_service.repositories.ItemRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final ItemRepository itemRepository;
    private final CheckoutMapper checkoutMapper;

    public CheckoutService(CheckoutRepository checkoutRepository, ItemRepository itemRepository, CheckoutMapper checkoutMapper) {
        this.checkoutRepository = checkoutRepository;
        this.itemRepository = itemRepository;
        this.checkoutMapper = checkoutMapper;
    }

    @Transactional
    public CheckoutResponse createCheckout() {
        Checkout checkout = Checkout.create();
        Checkout savedCheckout = checkoutRepository.save(checkout);
        return checkoutMapper.mapToCheckoutResponse(savedCheckout, null);
    }

    public List<CheckoutResponse> getAllCheckouts() {
        List<Checkout> checkouts = checkoutRepository.findAll();
        List<CheckoutResponse> checkoutResponses = new ArrayList<>();
        for (Checkout checkout : checkouts) {
            List<ItemDetails> itemDetails = getDetailedItemsListFromCheckoutItems(checkout);
            checkoutResponses.add(checkoutMapper.mapToCheckoutResponse(checkout, itemDetails));
        }
        return checkoutResponses;
    }

    public Map<String, Integer> getItemsByCheckoutId(Long checkoutId) {
        return checkoutRepository.findItemsByCheckoutId(checkoutId);
    }

    @Transactional
    public CheckoutResponse addItemsToCheckout(Long checkoutId, List<AddItemsRequest> items) {
        Checkout existingCheckout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException(checkoutId));

        areItemsAvailable(items.stream().map(AddItemsRequest::itemName).toList());

        existingCheckout = addItemsToCheckout(items, existingCheckout);

        List<ItemDetails> itemDetails = getDetailedItemsListFromCheckoutItems(existingCheckout);

        existingCheckout = updatePrices(existingCheckout, itemDetails);

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout, itemDetails);
    }

    public void areItemsAvailable(List<String> itemNames) {

        List<String> allAvailableItemNames = itemRepository.findAllAvailableItemNames();
        for (String itemName : itemNames) {
            if (!allAvailableItemNames.contains(itemName)) {
                throw new ItemUnavailableException(itemName);
            }
        }
    }

    public Checkout addItemsToCheckout(List<AddItemsRequest> newItems, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();
        boolean itemUpdated = false;

        for (AddItemsRequest item : newItems) {
            String itemName = item.itemName();
            int quantity = item.quantity();

            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                if (itemName.equals(entry.getKey())) {
                    entry.setValue(entry.getValue() + quantity);
                    itemUpdated = true;
                }
            }
            if (!itemUpdated) {
                items.put(itemName, quantity);
            }
        }
        checkout.setItems(items);
        return checkout;
    }

    @Transactional
    public CheckoutResponse deleteItemsFromCheckout(Long id, List<AddItemsRequest> items) {

        Checkout existingCheckout = checkoutRepository.findById(id)
                .orElseThrow(() -> new CheckoutNotFoundException(id));

        List<String> itemNamesToDelete = items.stream().map(AddItemsRequest::itemName).toList();
        List<String> itemNamesInCheckout = existingCheckout.getItems().keySet().stream().toList();

        // check if all items are available in checkout
        if (!itemNamesInCheckout.containsAll(itemNamesToDelete)) {
            throw new ItemNotFoundInCheckout(itemNamesToDelete.stream().filter(itemName -> !itemNamesInCheckout.contains(itemName)).findFirst().orElse(""));
        }

        existingCheckout = deleteItemsFromCheckout(items, existingCheckout);

        // get item details
        List<ItemDetails> itemDetails = getDetailedItemsListFromCheckoutItems(existingCheckout);

        if (!itemDetails.isEmpty()) {
            existingCheckout = updatePrices(existingCheckout, itemDetails);
        } else {
            existingCheckout.setFinalPrice(BigDecimal.ZERO);
            existingCheckout.setTotalDiscount(BigDecimal.ZERO);
            existingCheckout.setPriceBeforeDiscount(BigDecimal.ZERO);
        }

        checkoutRepository.save(existingCheckout);

        return checkoutMapper.mapToCheckoutResponse(existingCheckout, itemDetails);
    }

    public Checkout deleteItemsFromCheckout(List<AddItemsRequest> itemsToRemove, Checkout checkout) {
        Map<String, Integer> items = checkout.getItems();

        for (AddItemsRequest item : itemsToRemove) {
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
    @Transactional
    public void deleteCheckout(Long id) {
        if (!checkoutRepository.existsById(id)) {
            throw new CheckoutNotFoundException(id);
        }
        checkoutRepository.deleteById(id);
    }

    public List<ItemDetails> getDetailedItemsListFromCheckoutItems(Checkout checkout) {
        if (checkout == null) {
            return null;
        }
        List<ItemDetails> itemDetailsList = new ArrayList<>();
        List<Item> availableItems = itemRepository.findAll();
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
                    ItemDetails itemDetails = ItemDetails.builder()
                            .itemName(itemName)
                            .quantity((int) itemQuantity)
                            .discountedQuantity(requiredQuantity)
                            .unitPrice(item.getNormalPrice())
                            .discountApplies((int) itemQuantity >= requiredQuantity)
                            .priceBeforeDiscount(priceBeforeDiscount)
                            .discountAmount(discount)
                            .finalPrice(finalPrice)
                            .build();
                    itemDetailsList.add(itemDetails);
                    break;
                }
            }
        }
        return itemDetailsList;
    }

    public Checkout updatePrices(Checkout checkout, List<ItemDetails> itemDetailsList) {
        if (checkout.getItems().isEmpty() || itemDetailsList.isEmpty()) {
            return checkout;
        }

        BigDecimal priceBeforeDiscount = new BigDecimal(0);
        BigDecimal totalDiscount = new BigDecimal(0);
        BigDecimal finalPrice = new BigDecimal(0);

        for (ItemDetails itemDetails : itemDetailsList) {
            priceBeforeDiscount = priceBeforeDiscount.add(itemDetails.priceBeforeDiscount());
            totalDiscount = totalDiscount.add(itemDetails.discountAmount());
            finalPrice = finalPrice.add(itemDetails.finalPrice());
        }

        checkout.setPriceBeforeDiscount(priceBeforeDiscount);
        checkout.setTotalDiscount(totalDiscount);
        checkout.setFinalPrice(finalPrice);
        return checkout;
    }

    public ReceiptResponse pay(Long id) {
        return null;
    }
}
