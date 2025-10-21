package pl.paweldyjak.checkout_service.dtos.response;

import lombok.Builder;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ReceiptResponse(
        Long checkoutId,
        String paymentDate,
        List<ReceiptItemDetails> items,
        CheckoutStatus status,
        BigDecimal priceBeforeDiscount,
        BigDecimal quantityDiscount,
        BigDecimal bundleDiscount,
        BigDecimal totalDiscount,
        BigDecimal finalPrice
) {}
