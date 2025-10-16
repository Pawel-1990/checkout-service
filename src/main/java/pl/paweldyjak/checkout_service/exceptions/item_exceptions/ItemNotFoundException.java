package pl.paweldyjak.checkout_service.exceptions.item_exceptions;

public class ItemNotFoundException extends ItemException{
    public ItemNotFoundException(Long itemId) {
        super("Item with ID " + itemId + " does not exist");
    }
}
