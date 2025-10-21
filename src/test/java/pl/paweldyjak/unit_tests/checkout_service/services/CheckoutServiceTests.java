package pl.paweldyjak.unit_tests.checkout_service.services;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import pl.paweldyjak.checkout_service.CheckoutServiceApplication;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemInfo;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptItemDetails;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.entities.Checkout;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;
import pl.paweldyjak.checkout_service.mappers.CheckoutMapper;
import pl.paweldyjak.checkout_service.repositories.CheckoutRepository;
import pl.paweldyjak.checkout_service.services.CheckoutService;
import pl.paweldyjak.checkout_service.services.ItemService;
import pl.paweldyjak.unit_tests.checkout_service.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CheckoutServiceApplication.class)
@ExtendWith(MockitoExtension.class)
public class CheckoutServiceTests {
    private final long id = 1L;
    private CheckoutService checkoutService;

    @Mock private CheckoutRepository checkoutRepository;
    @Mock private ItemService itemService;
    private CheckoutMapper checkoutMapper;

    @BeforeEach
    void setup() {
        checkoutMapper = new CheckoutMapper();
        checkoutService = new CheckoutService(checkoutRepository, itemService, checkoutMapper);
    }

    @Test
    public void testGetAllCheckouts() {
        Checkout checkout = Utils.buildCheckout();
        when(checkoutRepository.findAll()).thenReturn(Collections.singletonList(checkout));
        List<CheckoutResponse> checkoutResponse = Collections.singletonList(Utils.buildCheckoutResponse(id));
        List<CheckoutResponse> actualResponse = checkoutService.getAllCheckouts();

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(checkoutResponse);

        verify(checkoutRepository, times(1)).findAll();
        verifyNoMoreInteractions(checkoutRepository);
        verifyNoInteractions(itemService);
    }

    @Test
    public void testGetCheckoutById() {
        Checkout checkout = Utils.buildCheckout();
        CheckoutResponse expectedResponse = Utils.buildCheckoutResponse(id);

        when(checkoutRepository.findById(id)).thenReturn(java.util.Optional.of(checkout));

        CheckoutResponse actualResponse = checkoutService.getCheckoutById(id);

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedResponse);

        verify(checkoutRepository, times(1)).findById(id);
        verifyNoMoreInteractions(checkoutRepository);
        verifyNoInteractions(itemService);
    }

    @Test
    public void testCreateCheckout() {
        Checkout checkout = Utils.buildCheckout();
        CheckoutResponse expectedResponse = Utils.buildCheckoutResponse(id);
        when(checkoutRepository.save(any())).thenReturn(checkout);
        CheckoutResponse actualResponse = checkoutService.createCheckout();

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedResponse);

        verify(checkoutRepository, times(1)).save(any());
        verifyNoMoreInteractions(checkoutRepository);
        verifyNoInteractions(itemService);
    }

    @Test
    public void testAddItemsToCheckout() {
        Checkout checkout = Utils.buildCheckout();
        CheckoutResponse expectedResponse = CheckoutResponse.builder()
                .id(id)
                .status(CheckoutStatus.ACTIVE)
                .items(Collections.singletonList(CheckoutItemInfo.builder().itemName("Apple").quantity(11).build()))
                .priceBeforeDiscount(BigDecimal.valueOf(50))
                .totalDiscount(BigDecimal.valueOf(10))
                .finalPrice(BigDecimal.valueOf(40))
                .build();
        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));
        when(itemService.getAllAvailableItemNames()).thenReturn(List.of("Apple"));
        CheckoutResponse actualResponse = checkoutService.addItemsToCheckout(id,
                List.of(CheckoutItemInfo.builder().itemName("Apple").quantity(5).build()));

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedResponse);

        verify(checkoutRepository, times(1)).findById(id);
        verify(itemService, times(1)).getAllAvailableItemNames();
    }

    @Test
    public void testAreItemsAvailable() {
        when(itemService.getAllAvailableItemNames()).thenReturn(List.of("Apple"));
        assertDoesNotThrow(() -> checkoutService.areItemsAvailable(List.of("Apple")));
    }

    @Test
    public void testDeleteItemsFromCheckout() {
        Checkout checkout = Utils.buildCheckout();
        List<Item> itemEntities = Collections.singletonList(Utils.buildItem(id));
        CheckoutResponse expectedResponse = CheckoutResponse.builder()
                .id(id)
                .status(CheckoutStatus.ACTIVE)
                .items(Collections.singletonList(CheckoutItemInfo.builder().itemName("Apple").quantity(1).build()))
                .priceBeforeDiscount(BigDecimal.valueOf(50))
                .totalDiscount(BigDecimal.valueOf(0))
                .finalPrice(BigDecimal.valueOf(50))
                .build();
        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));
        when(itemService.getAllItemsEntities()).thenReturn(itemEntities);

        CheckoutResponse actualResponse = checkoutService.deleteItemsFromCheckout(id,
                List.of(CheckoutItemInfo.builder().itemName("Apple").quantity(5).build()));

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedResponse);

        verify(checkoutRepository, times(1)).findById(id);
        verify(itemService, times(1)).getAllItemsEntities();
    }

    @Test
    public void deleteCheckout() {
        when(checkoutRepository.existsById(id)).thenReturn(true);

        checkoutService.deleteCheckout(id);

        verify(checkoutRepository, times(1)).existsById(id);
        verify(checkoutRepository, times(1)).deleteById(id);
    }

    @Test
    public void testPay() {
        Checkout checkout = buildCheckoutForPay();

        ReceiptItemDetails receiptItemDetails = buildReceiptItemDetailsForPay();

        when(checkoutRepository.findById(id)).thenReturn(Optional.of(checkout));

        List<Item> itemEntities = Collections.singletonList(Utils.buildItem(id));
        when(itemService.getAllItemsEntities()).thenReturn(itemEntities);
        when(checkoutRepository.save(any())).thenReturn(checkout);

        ReceiptResponse receiptResponse = buildReceiptResponseForPay(id, receiptItemDetails);

        ReceiptResponse actualResponse = checkoutService.pay(id);

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("paymentDate")
                .isEqualTo(receiptResponse);

        verify(checkoutRepository, times(1)).findById(id);
        verify(itemService, times(1)).getAllItemsEntities();
        verify(checkoutRepository, times(1)).save(any());
    }

    public static ReceiptResponse buildReceiptResponseForPay(Long id, ReceiptItemDetails receiptItemDetails) {
        return ReceiptResponse.builder()
                .checkoutId(id)
                .status(CheckoutStatus.PAID)
                .items(Collections.singletonList(receiptItemDetails))
                .totalDiscount(BigDecimal.valueOf(10))
                .priceBeforeDiscount(BigDecimal.valueOf(50))
                .finalPrice(BigDecimal.valueOf(40))
                .build();
    }

    public static ReceiptItemDetails buildReceiptItemDetailsForPay() {
        return ReceiptItemDetails.builder()
                .itemName("Apple")
                .quantity(6)
                .discountedQuantity(3)
                .unitPrice(BigDecimal.valueOf(50))
                .discountApplies(true)
                .priceBeforeDiscount(BigDecimal.valueOf(300))
                .priceAfterDiscount(BigDecimal.valueOf(240))
                .discountAmount(BigDecimal.valueOf(60))
                .build();
    }

    public static Checkout buildCheckoutForPay() {
        Checkout checkout = new Checkout();
        Map<String, Integer> items = new HashMap<>(Map.of("Apple", 6));
        checkout.setId(1L);
        checkout.setCreatedAt(LocalDateTime.now());
        checkout.setStatus(CheckoutStatus.PAID);
        checkout.setItems(items);
        checkout.setPriceBeforeDiscount(BigDecimal.valueOf(50));
        checkout.setTotalDiscount(BigDecimal.valueOf(10));
        checkout.setFinalPrice(BigDecimal.valueOf(40));
        return checkout;
    }

}
