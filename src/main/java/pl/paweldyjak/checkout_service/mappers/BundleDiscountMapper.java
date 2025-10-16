package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dto.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.dto.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Item;

@Component
public class BundleDiscountMapper {

    private final ItemMapper itemMapper;

    public BundleDiscountMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public BundleDiscountResponse convertToBundleDiscountResponse(BundleDiscount bundleDiscount) {
        if (bundleDiscount == null) {
            return null;
        }

        return new BundleDiscountResponse(
                bundleDiscount.getId(),
                convertToItemResponse(bundleDiscount.getFirstItem()),
                convertToItemResponse(bundleDiscount.getSecondItem()),
                bundleDiscount.getDiscountAmount(),
                bundleDiscount.isActive()
        );
    }

    private ItemResponse convertToItemResponse(Item item) {
        if (item == null) {
            return null;
        }
        ItemResponse response = itemMapper.toItemResponse(item);

        response.setRequiredQuantity(null);
        response.setSpecialPrice(null);

        return response;
    }
}

