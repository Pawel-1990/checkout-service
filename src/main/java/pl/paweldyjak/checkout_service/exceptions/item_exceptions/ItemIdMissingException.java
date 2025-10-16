package pl.paweldyjak.checkout_service.exceptions.item_exceptions;

public class ItemIdMissingException extends ItemException {
    public ItemIdMissingException() {
        super("Item ID is required for update operation");
    }
}
