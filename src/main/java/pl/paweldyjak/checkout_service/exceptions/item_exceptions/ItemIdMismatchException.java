package pl.paweldyjak.checkout_service.exceptions.item_exceptions;

public class ItemIdMismatchException extends ItemException {
    public ItemIdMismatchException(Long pathId, Long bodyId) {
        super(String.format("ID mismatch: path variable has ID %d, but request body has ID %d", pathId, bodyId));
    }
}

