package pl.paweldyjak.checkout_service.unit_tests.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.exceptions.bundle_discount_exceptions.BundleDiscountNotFoundException;
import pl.paweldyjak.checkout_service.mappers.BundleDiscountMapper;
import pl.paweldyjak.checkout_service.repositories.BundleDiscountRepository;
import pl.paweldyjak.checkout_service.services.BundleDiscountService;
import pl.paweldyjak.checkout_service.services.ItemService;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class BundleDiscountExceptionsTest {
    @Mock private BundleDiscountRepository bundleDiscountRepository;
    @Mock private ItemService itemService;
    @Mock private BundleDiscountMapper bundleDiscountMapper;

    private BundleDiscountService bundleDiscountService;

    @BeforeEach
    void setup() {
        bundleDiscountService = new BundleDiscountService(bundleDiscountRepository, bundleDiscountMapper, itemService);
    }

    @Test
    public void testBundleDiscountNotFoundException() {
        Mockito.when(bundleDiscountRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BundleDiscountNotFoundException.class, () -> bundleDiscountService.getBundleDiscountResponseById(1L));
    }
}
