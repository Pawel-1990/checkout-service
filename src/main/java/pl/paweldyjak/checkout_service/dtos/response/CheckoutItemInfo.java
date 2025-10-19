package pl.paweldyjak.checkout_service.dtos.response;

import lombok.Builder;

@Builder
public record CheckoutItemInfo(
        String itemName,
        int quantity
) {
}
