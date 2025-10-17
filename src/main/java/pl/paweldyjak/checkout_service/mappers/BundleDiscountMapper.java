package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dto.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dto.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.dto.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemNotFoundException;

import java.math.BigDecimal;

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

        return new BundleDiscountResponse(
                bundleDiscount.getId(),
                mapToItemResponse(bundleDiscount.getFirstItem()),
                mapToItemResponse(bundleDiscount.getSecondItem()),
                bundleDiscount.getDiscountAmount()
        );
    }

    private ItemResponse mapToItemResponse(Item item) {
        if (item == null) {
            return null;
        }
        ItemResponse response = itemMapper.mapToItemResponse(item);
        response.setRequiredQuantity(null);
        response.setSpecialPrice(null);
        return response;
    }

    public BundleDiscount mapToBundleDiscountEntity(BundleDiscountRequest request, Item firstItem, Item secondItem) {
        if (request == null) {
            return null;
        }
        BundleDiscount bundleDiscountEntity = new BundleDiscount();
        bundleDiscountEntity.setDiscountAmount(request.getDiscountAmount());
        bundleDiscountEntity.setFirstItem(firstItem);
        bundleDiscountEntity.setSecondItem(secondItem);
        return bundleDiscountEntity;
    }
}

