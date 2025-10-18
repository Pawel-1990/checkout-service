package pl.paweldyjak.checkout_service.exceptions.checkout_exceptions;

public class InaccurateQuantityToDeleteException extends CheckoutException {
    public InaccurateQuantityToDeleteException(String itemName, int quantityToDelete, int quantityInCheckout) {
        super(String.format("Cannot delete %d %s(s) - there %s only %d in checkout", quantityToDelete, itemName,
                (quantityInCheckout == 1 ? "is" : "are"), quantityInCheckout));
    }
}
