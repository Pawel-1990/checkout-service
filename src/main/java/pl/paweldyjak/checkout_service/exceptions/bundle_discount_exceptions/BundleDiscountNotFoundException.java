package pl.paweldyjak.checkout_service.exceptions.bundle_discount_exceptions;

public class BundleDiscountNotFoundException extends BundleDiscountException{
    public BundleDiscountNotFoundException(Long bundleDiscountId) {
        super("Bundle discount with ID " + bundleDiscountId + " does not exist");
    }
}
