package pl.paweldyjak.checkout_service.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record BundleDiscountRequestDto(
        @NotNull(message = "First item ID cannot be null")
        Long firstItemId,

        @NotNull(message = "Second item ID cannot be null")
        Long secondItemId,

        @NotNull(message = "Discount amount cannot be null")
        @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
        BigDecimal discountAmount


) {
}
