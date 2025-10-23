package pl.paweldyjak.checkout_service.unit_tests.mappers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.dtos.CheckoutItem;
import pl.paweldyjak.checkout_service.mappers.CheckoutMapper;
import pl.paweldyjak.checkout_service.unit_tests.utils.Utils;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.entities.Checkout;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CheckoutMapperTests {
    private CheckoutMapper checkoutMapper;

    @BeforeEach
    void setup() {
        checkoutMapper = new CheckoutMapper();
    }

    @Test
    public void testMapToCheckoutResponse() {
        Checkout checkout = Utils.buildCheckout();

        CheckoutResponse expectedResponse = CheckoutResponse.builder()
                .id(1L)
                .createdAt(checkout.getCreatedAt())
                .status(checkout.getStatus())
                .items(Collections.singletonList(new CheckoutItem("Apple", 6)))
                .priceBeforeDiscount(checkout.getPriceBeforeDiscount())
                .totalDiscount(checkout.getTotalDiscount())
                .finalPrice(checkout.getFinalPrice())
                .build();

        CheckoutResponse actualResponse = checkoutMapper.mapToCheckoutResponse(checkout);

        assert expectedResponse.equals(actualResponse);

    }

    @Test
    public void testMapToReceiptResponse() {
        Checkout checkout = Utils.buildCheckout();

        ReceiptResponse expectedResponse = ReceiptResponse.builder()
                .checkoutId(1L)
                .status(checkout.getStatus())
                .items(Collections.emptyList())
                .priceBeforeDiscount(checkout.getPriceBeforeDiscount())
                .totalDiscount(checkout.getTotalDiscount())
                .finalPrice(checkout.getFinalPrice())
                .build();

        ReceiptResponse actualResponse = checkoutMapper.mapToReceiptResponse(checkout, Collections.emptyList());

        // ignoring the payment date field as it is generated on the fly
        Assertions.assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("paymentDate")
                .isEqualTo(expectedResponse);
    }

    @Test
    public void testMapItemsToCheckoutItemInfo() {
        Checkout checkout = Utils.buildCheckout();
        List<CheckoutItem> expectedResponse = List.of(new CheckoutItem("Apple", 6));

        assert expectedResponse.equals(checkoutMapper.mapItemsToCheckoutItemInfo(checkout));
    }
}
