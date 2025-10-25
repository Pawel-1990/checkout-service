package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemDto;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponseDto;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutItemDetailsDto;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponseDto;
import pl.paweldyjak.checkout_service.entities.Checkout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class CheckoutMapper {

    public CheckoutResponseDto mapToCheckoutResponse(Checkout checkout) {
        if (checkout == null) {
            return null;
        }
        return CheckoutResponseDto.builder()
                .id(checkout.getId())
                .createdAt(checkout.getCreatedAt())
                .status(checkout.getStatus())
                .items(mapItemsToCheckoutItemInfo(checkout))
                .priceBeforeDiscount(checkout.getPriceBeforeDiscount())
                .totalDiscount(checkout.getTotalDiscount())
                .quantityDiscount(checkout.getQuantityDiscount())
                .bundleDiscount(checkout.getBundleDiscount())
                .finalPrice(checkout.getFinalPrice())
                .build();
    }

    public ReceiptResponseDto mapToReceiptResponse(Checkout checkout, List<CheckoutItemDetailsDto> checkoutItemDetailDtos) {
        return ReceiptResponseDto.builder()
                .checkoutId(checkout.getId())
                .paymentDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME))
                .status(checkout.getStatus())
                .items(checkoutItemDetailDtos)
                .priceBeforeDiscount(checkout.getPriceBeforeDiscount())
                .totalDiscount(checkout.getTotalDiscount())
                .quantityDiscount(checkout.getQuantityDiscount())
                .bundleDiscount(checkout.getBundleDiscount())
                .finalPrice(checkout.getFinalPrice())
                .build();
    }

    public List<CheckoutItemDto> mapItemsToCheckoutItemInfo(Checkout checkout) {
        if (checkout == null) {
            return Collections.emptyList();
        }
        List<CheckoutItemDto> checkoutItemDto = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : checkout.getItems().entrySet()) {
            checkoutItemDto.add(CheckoutItemDto.builder()
                    .itemName(entry.getKey())
                    .quantity(entry.getValue())
                    .build());
        }
        return checkoutItemDto;
    }
}
