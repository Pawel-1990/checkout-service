package pl.paweldyjak.checkout_service.dtos.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReceiptResponse(
        String checkoutId,
        LocalDateTime paymentDate,
        List<ItemDetails> items,
        BigDecimal priceWithoutDiscount,
        BigDecimal totalDiscount,
        BigDecimal finalPrice
) {}
