package pl.paweldyjak.checkout_service.dtos.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record BundleDiscountResponseDto(
        Long id,
        ItemResponseDto firstItem,
        ItemResponseDto secondItem,
        BigDecimal discountAmount
) {

}

