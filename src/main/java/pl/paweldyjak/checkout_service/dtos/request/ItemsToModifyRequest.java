package pl.paweldyjak.checkout_service.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ItemsToModifyRequest(
    @NotBlank(message = "Item name cannot be blank")
    String itemName,

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {}
