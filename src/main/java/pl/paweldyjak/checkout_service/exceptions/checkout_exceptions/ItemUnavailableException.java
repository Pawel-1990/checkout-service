package pl.paweldyjak.checkout_service.exceptions.checkout_exceptions;

public class ItemUnavailableException extends CheckoutException{
    public ItemUnavailableException(String itemName) {
        super(String.format("Cannot add item - %s is unavailable", itemName));
    }
}
