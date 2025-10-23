package pl.paweldyjak.checkout_service.unit_tests.exceptions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemInfo;
import pl.paweldyjak.checkout_service.entities.Checkout;
import pl.paweldyjak.checkout_service.exceptions.checkout_exceptions.*;
import pl.paweldyjak.checkout_service.mappers.CheckoutMapper;
import pl.paweldyjak.checkout_service.repositories.CheckoutRepository;
import pl.paweldyjak.checkout_service.services.BundleDiscountService;
import pl.paweldyjak.checkout_service.services.CheckoutService;
import pl.paweldyjak.checkout_service.services.ItemService;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutExceptionsTest {
    @Mock private CheckoutRepository checkoutRepository;
    @Mock private ItemService itemService;
    @Mock private CheckoutMapper checkoutMapper;
    @Mock private BundleDiscountService bundleDiscountService;

    CheckoutService checkoutService;


    @BeforeEach
    void setup() {
        checkoutService = new CheckoutService(checkoutRepository, itemService, checkoutMapper, bundleDiscountService);
    }

    @Test
    public void testCheckoutNotFoundException() {
        when(checkoutRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CheckoutNotFoundException.class, () -> checkoutService.getCheckoutById(1L));
    }

    @Test
    public void testEmptyCheckoutException() {
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(new Checkout()));

        assertThrows(EmptyCheckoutException.class, () -> checkoutService.pay(1L));
    }

    @Test
    public void testInaccurateException() {
        Checkout checkout = new Checkout();
        checkout.setItems(Map.of("Apple", 2));

        List<CheckoutItemInfo> checkoutItemInfos = Collections.singletonList(CheckoutItemInfo.builder()
                .itemName("Apple").quantity(5).build());

        assertThrows(InaccurateQuantityToDeleteException.class, () -> checkoutService.deleteItemsFromCheckout(checkoutItemInfos, checkout));
    }

    @Test
    public void testItemNotFoundInCheckoutException() {
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(new Checkout()));
        List<CheckoutItemInfo> checkoutItemInfos =
                Collections.singletonList(CheckoutItemInfo.builder()
                .itemName("Apple").quantity(5).build());
        checkoutService.deleteItemsFromCheckout(checkoutItemInfos, new Checkout());

        assertThrows(ItemNotFoundInCheckout.class, () -> checkoutService.deleteItemsFromCheckout(1L, checkoutItemInfos));
    }

    @Test
    public void testItemUnavailableException() {
        assertThrows(ItemUnavailableException.class, () -> checkoutService.areItemsAvailable(List.of("Apple")));
    }
}
