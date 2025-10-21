package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemInfo;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptItemDetails;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.entities.Checkout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class CheckoutMapper {

    public CheckoutResponse mapToCheckoutResponse(Checkout checkout) {
        if (checkout == null) {
            return null;
        }
        return CheckoutResponse.builder()
                .id(checkout.getId())
                .createdAt(checkout.getCreatedAt())
                .status(checkout.getStatus())
                .items(mapItemsToCheckoutItemInfo(checkout))
                .priceBeforeDiscount(checkout.getPriceBeforeDiscount())
                .totalDiscount(checkout.getTotalDiscount())
                .finalPrice(checkout.getFinalPrice())
                .build();
    }

    public ReceiptResponse mapToReceiptResponse(Checkout checkout, List<ReceiptItemDetails> receiptItemDetails) {
        return ReceiptResponse.builder()
                .checkoutId(checkout.getId())
                .paymentDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME))
                .status(checkout.getStatus())
                .items(receiptItemDetails)
                .priceBeforeDiscount(checkout.getPriceBeforeDiscount())
                .totalDiscount(checkout.getTotalDiscount())
                .finalPrice(checkout.getFinalPrice())
                .build();
    }

    public List<CheckoutItemInfo> mapItemsToCheckoutItemInfo(Checkout checkout) {
        if (checkout == null) {
            return Collections.emptyList();
        }
        List<CheckoutItemInfo> checkoutItemInfo = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : checkout.getItems().entrySet()) {
            checkoutItemInfo.add(CheckoutItemInfo.builder()
                    .itemName(entry.getKey())
                    .quantity(entry.getValue())
                    .build());
        }
        return checkoutItemInfo;
    }
}
