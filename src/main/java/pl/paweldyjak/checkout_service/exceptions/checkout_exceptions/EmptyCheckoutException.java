package pl.paweldyjak.checkout_service.exceptions.checkout_exceptions;

public class EmptyCheckoutException extends CheckoutException{
    public EmptyCheckoutException() {
        super("Checkout is empty");
    }
}
