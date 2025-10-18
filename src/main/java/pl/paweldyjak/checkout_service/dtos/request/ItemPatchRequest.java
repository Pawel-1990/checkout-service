package pl.paweldyjak.checkout_service.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record ItemPatchRequest(
        String name,

        @DecimalMin(value = "0.01", message = "Normal price must be greater than 0")
        BigDecimal normalPrice,

        @Min(value = 2, message = "Required quantity must be at least 2")
        Integer requiredQuantity,

        @DecimalMin(value = "0.01", message = "Special price must be greater than 0")
        BigDecimal specialPrice) {
}
