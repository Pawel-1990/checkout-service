package pl.paweldyjak.checkout_service.dtos.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReceiptItemDetails(
        String itemName,
        int quantity,
        int discountedQuantity,
        BigDecimal unitPrice,
        boolean discountApplies,
        BigDecimal priceBeforeDiscount,
        BigDecimal discountAmount,
        BigDecimal priceAfterDiscount
) {}
