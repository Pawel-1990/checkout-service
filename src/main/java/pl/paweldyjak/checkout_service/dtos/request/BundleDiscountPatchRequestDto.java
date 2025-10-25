package pl.paweldyjak.checkout_service.dtos.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;


public record BundleDiscountPatchRequestDto(
        Long firstItemId,
        Long secondItemId,

        @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
        BigDecimal discountAmount) {
}
