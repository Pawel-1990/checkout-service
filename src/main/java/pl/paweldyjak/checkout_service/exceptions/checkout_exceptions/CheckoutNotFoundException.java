package pl.paweldyjak.checkout_service.exceptions.checkout_exceptions;

public class CheckoutNotFoundException extends CheckoutException{
    public CheckoutNotFoundException(Long checkoutId) {
        super("Checkout with ID " + checkoutId + " does not exist");
    }
}
