package pl.paweldyjak.checkout_service.dtos.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CheckoutItemDetails(
        String itemName,
        int quantity,
        int discountedQuantity,
        BigDecimal unitPrice,
        boolean quantityDiscountApplies,
        BigDecimal priceBeforeDiscount,
        BigDecimal discountAmount,
        BigDecimal priceAfterDiscount
) {}
