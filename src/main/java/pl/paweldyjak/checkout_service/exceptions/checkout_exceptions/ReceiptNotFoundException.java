package pl.paweldyjak.checkout_service.exceptions.checkout_exceptions;

public class ReceiptNotFoundException extends CheckoutException {
    public ReceiptNotFoundException(Long checkoutId) {
        super("Receipt for checkout with ID " + checkoutId + " does not exist");
    }
}
