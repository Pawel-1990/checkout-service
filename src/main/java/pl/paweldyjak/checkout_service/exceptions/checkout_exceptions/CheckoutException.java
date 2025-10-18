package pl.paweldyjak.checkout_service.exceptions.checkout_exceptions;

public class CheckoutException extends RuntimeException {
    public CheckoutException(String message) {
        super(message);
    }
}
