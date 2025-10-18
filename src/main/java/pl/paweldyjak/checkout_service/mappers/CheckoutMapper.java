package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ItemDetails;
import pl.paweldyjak.checkout_service.entities.Checkout;

import java.util.Collections;
import java.util.List;

@Component
public class CheckoutMapper {

    public CheckoutResponse mapToCheckoutResponse(Checkout checkout, List<ItemDetails> itemDetails) {
        if (checkout == null) {
            return null;
        }
        return CheckoutResponse.builder()
                .id(checkout.getId())
                .createdAt(checkout.getCreatedAt())
                .status(checkout.getStatus())
                .items(itemDetails != null ? itemDetails : Collections.emptyList())
                .priceBeforeDiscount(checkout.getPriceBeforeDiscount())
                .totalDiscount(checkout.getTotalDiscount())
                .finalPrice(checkout.getFinalPrice())
                .build();
    }
}
