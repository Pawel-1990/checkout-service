package pl.paweldyjak.checkout_service.dtos.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemRequest(

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotNull(message = "Normal price cannot be null")
        @DecimalMin(value = "0.01", message = "Normal price must be greater than 0")
        BigDecimal normalPrice,

        @Min(value = 2, message = "Required quantity must be at least 2")
        Integer requiredQuantity,

        @DecimalMin(value = "0.01", message = "Special price must be greater than 0")
        BigDecimal specialPrice
) {
}