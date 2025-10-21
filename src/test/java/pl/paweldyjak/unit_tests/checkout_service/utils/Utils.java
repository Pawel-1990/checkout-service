package pl.paweldyjak.unit_tests.checkout_service.utils;

import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequest;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequest;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemInfo;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Checkout;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static BundleDiscountRequest buildBundleDiscountRequest(Long id1, Long id2, BigDecimal discountAmount) {
        return new BundleDiscountRequest(id1, id2, discountAmount);
    }

    public static BundleDiscountPatchRequest buildBundleDiscountPatchRequest(Long id1, Long id2, BigDecimal discountAmount) {
        return new BundleDiscountPatchRequest(id1, id2, discountAmount);
    }

    public static BundleDiscountResponse buildBundleDiscountResponse(Long bundleDiscountId, Long itemResponseId1, Long itemResponseId2,
                                                                     BigDecimal discountAmount) {
        return new BundleDiscountResponse(bundleDiscountId, buildItemResponse(itemResponseId1), buildItemResponse(itemResponseId2), discountAmount);
    }

    public static BundleDiscount buildBundleDiscount(Long bundleDiscountId, Long firstItemId, Long secondItemId, BigDecimal discountAmount) {
        BundleDiscount bundleDiscount = new BundleDiscount();
        bundleDiscount.setId(bundleDiscountId);
        bundleDiscount.setFirstItem(buildItem(firstItemId));
        bundleDiscount.setSecondItem(buildItem(secondItemId));
        bundleDiscount.setDiscountAmount(discountAmount);
        return bundleDiscount;
    }

    public static CheckoutResponse buildCheckoutResponse(Long checkoutId) {
        return CheckoutResponse.builder()
                .id(checkoutId)
                .status(CheckoutStatus.ACTIVE)
                .items(Collections.singletonList(CheckoutItemInfo.builder().itemName("Apple").quantity(6).build()))
                .priceBeforeDiscount(BigDecimal.valueOf(50))
                .totalDiscount(BigDecimal.valueOf(10))
                .finalPrice(BigDecimal.valueOf(40))
                .build();
    }

    public static ItemResponse buildItemResponse(Long id) {
        return ItemResponse.builder()
                .id(id)
                .name("Apple")
                .normalPrice(BigDecimal.valueOf(50))
                .requiredQuantity(3)
                .specialPrice(BigDecimal.valueOf(40))
                .build();
    }

    public static ItemRequest buildItemRequest() {
        return ItemRequest.builder()
                .name("Apple")
                .normalPrice(BigDecimal.valueOf(50))
                .requiredQuantity(3)
                .specialPrice(BigDecimal.valueOf(40))
                .build();
    }

    public static Item buildItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("Apple");
        item.setNormalPrice(BigDecimal.valueOf(50));
        item.setRequiredQuantity(3);
        item.setSpecialPrice(BigDecimal.valueOf(40));
        return item;
    }

    public static Checkout buildCheckout() {
        Map<String, Integer> items = new HashMap<>(Map.of("Apple", 6));
        Checkout checkout = new Checkout();
        checkout.setId(1L);
        checkout.setCreatedAt(LocalDateTime.now());
        checkout.setStatus(CheckoutStatus.ACTIVE);
        checkout.setItems(items);
        checkout.setPriceBeforeDiscount(BigDecimal.valueOf(50));
        checkout.setTotalDiscount(BigDecimal.valueOf(10));
        checkout.setFinalPrice(BigDecimal.valueOf(40));

        return checkout;
    }
}
