package pl.paweldyjak.checkout_service.exceptions.checkout_exceptions;

public class ItemNotFoundInCheckout extends CheckoutException{
    public ItemNotFoundInCheckout(String itemName) {
        super(String.format("Item %s is not in checkout", itemName));
    }
}
