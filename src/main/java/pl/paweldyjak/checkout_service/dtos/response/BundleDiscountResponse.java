package pl.paweldyjak.checkout_service.dtos.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record BundleDiscountResponse(
        Long id,
        ItemResponse firstItem,
        ItemResponse secondItem,
        BigDecimal discountAmount
) {

}

