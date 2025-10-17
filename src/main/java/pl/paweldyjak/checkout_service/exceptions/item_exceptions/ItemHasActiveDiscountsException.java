package pl.paweldyjak.checkout_service.exceptions.item_exceptions;

public class ItemHasActiveDiscountsException extends ItemException {
    public ItemHasActiveDiscountsException(Long itemId) {
        super(String.format("Cannot delete item with ID %d because it has active bundle discounts", itemId));
    }
}

