package pl.paweldyjak.checkout_service.unit_tests.utils;

import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequestDto;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequestDto;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequestDto;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponseDto;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemDto;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponseDto;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponseDto;
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

    public static BundleDiscountRequestDto buildBundleDiscountRequest(Long id1, Long id2, BigDecimal discountAmount) {
        return new BundleDiscountRequestDto(id1, id2, discountAmount);
    }

    public static BundleDiscountPatchRequestDto buildBundleDiscountPatchRequest(Long id1, Long id2, BigDecimal discountAmount) {
        return new BundleDiscountPatchRequestDto(id1, id2, discountAmount);
    }

    public static BundleDiscountResponseDto buildBundleDiscountResponse(Long bundleDiscountId, Long itemResponseId1, Long itemResponseId2,
                                                                        BigDecimal discountAmount) {
        return new BundleDiscountResponseDto(bundleDiscountId, buildItemResponse(itemResponseId1), buildItemResponse(itemResponseId2), discountAmount);
    }

    public static BundleDiscount buildBundleDiscount(Long bundleDiscountId, Long firstItemId, Long secondItemId, BigDecimal discountAmount) {
        BundleDiscount bundleDiscount = new BundleDiscount();
        bundleDiscount.setId(bundleDiscountId);
        bundleDiscount.setFirstItem(buildItem(firstItemId));
        bundleDiscount.setSecondItem(buildItem(secondItemId));
        bundleDiscount.setDiscountAmount(discountAmount);
        return bundleDiscount;
    }

    public static CheckoutResponseDto buildCheckoutResponse(Long checkoutId) {
        return CheckoutResponseDto.builder()
                .id(checkoutId)
                .status(CheckoutStatus.ACTIVE)
                .items(Collections.singletonList(CheckoutItemDto.builder().itemName("Apple").quantity(6).build()))
                .priceBeforeDiscount(BigDecimal.valueOf(50))
                .totalDiscount(BigDecimal.valueOf(10))
                .finalPrice(BigDecimal.valueOf(40))
                .build();
    }

    public static ItemResponseDto buildItemResponse(Long id) {
        return ItemResponseDto.builder()
                .id(id)
                .name("Apple")
                .normalPrice(BigDecimal.valueOf(50))
                .requiredQuantity(3)
                .specialPrice(BigDecimal.valueOf(40))
                .build();
    }

    public static ItemRequestDto buildItemRequest() {
        return ItemRequestDto.builder()
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
