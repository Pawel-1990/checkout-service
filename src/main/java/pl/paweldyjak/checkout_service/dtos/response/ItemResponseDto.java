package pl.paweldyjak.checkout_service.dtos.response;

import lombok.*;

import java.math.BigDecimal;


@Builder
public record ItemResponseDto(
        Long id,
        String name,
        BigDecimal normalPrice,
        Integer requiredQuantity,
        BigDecimal specialPrice
) {
}


