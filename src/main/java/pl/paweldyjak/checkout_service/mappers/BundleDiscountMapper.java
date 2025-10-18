package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Item;

@Component
public class BundleDiscountMapper {

    private final ItemMapper itemMapper;

    public BundleDiscountMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public BundleDiscountResponse mapToBundleDiscountResponse(BundleDiscount bundleDiscount) {
        if (bundleDiscount == null) {
            return null;
        }

        return BundleDiscountResponse.builder()
                .id(bundleDiscount.getId())
                .firstItem(mapToItemResponse(bundleDiscount.getFirstItem()))
                .secondItem(mapToItemResponse(bundleDiscount.getSecondItem()))
                .discountAmount(bundleDiscount.getDiscountAmount()).build();

    }

    private ItemResponse mapToItemResponse(Item item) {
        if (item == null) {
            return null;
        }
        return itemMapper.mapToItemResponse(item);
    }

    public BundleDiscount mapToBundleDiscountEntity(BundleDiscountRequest request, Item firstItem, Item secondItem) {
        if (request == null) {
            return null;
        }
        BundleDiscount bundleDiscountEntity = new BundleDiscount();
        bundleDiscountEntity.setDiscountAmount(request.discountAmount());
        bundleDiscountEntity.setFirstItem(firstItem);
        bundleDiscountEntity.setSecondItem(secondItem);
        return bundleDiscountEntity;
    }
}

