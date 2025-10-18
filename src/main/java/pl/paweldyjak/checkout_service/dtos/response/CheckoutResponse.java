package pl.paweldyjak.checkout_service.dtos.response;

import lombok.Builder;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CheckoutResponse(
        Long id,
        LocalDateTime createdAt,
        CheckoutStatus status,
        List<ItemDetails> items,
        BigDecimal priceBeforeDiscount,
        BigDecimal totalDiscount,
        BigDecimal finalPrice
) {}
