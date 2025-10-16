package pl.paweldyjak.checkout_service.exceptions.item_exceptions;

public class ItemAlreadyHasIdException extends ItemException {
    public ItemAlreadyHasIdException(Long id) {
        super("ID must be null for new items, but was: " + id);
    }
}
