package pl.paweldyjak.checkout_service.dtos.response;

import lombok.Builder;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemDto;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CheckoutResponseDto(
        Long id,
        LocalDateTime createdAt,
        CheckoutStatus status,
        List<CheckoutItemDto> items,
        BigDecimal priceBeforeDiscount,
        BigDecimal quantityDiscount,
        BigDecimal bundleDiscount,
        BigDecimal totalDiscount,
        BigDecimal finalPrice
) {}
