package pl.paweldyjak.unit_tests.checkout_service.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequest;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.mappers.BundleDiscountMapper;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.repositories.BundleDiscountRepository;
import pl.paweldyjak.checkout_service.services.BundleDiscountService;
import pl.paweldyjak.checkout_service.services.ItemService;
import pl.paweldyjak.unit_tests.checkout_service.utils.Utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BundleDiscountServiceTests {
    private final long id = 1L;
    private final long id2 = 2L;
    private BundleDiscountService bundleDiscountService;

    @Mock
    private BundleDiscountRepository bundleDiscountRepository;

    private ItemMapper itemMapper;

    BundleDiscountMapper bundleDiscountMapper;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setup() {
        itemMapper = new ItemMapper();
        bundleDiscountMapper = new BundleDiscountMapper(itemMapper);
        bundleDiscountService = new BundleDiscountService(bundleDiscountRepository, bundleDiscountMapper, itemService);
    }

    @Test
    public void testGetAllBundledDiscounts() {
        BundleDiscount bundleDiscount = Utils.buildBundleDiscount(id, id, id2, BigDecimal.TEN);
        List<BundleDiscountResponse> bundleDiscountResponses = Collections.singletonList(Utils.buildBundleDiscountResponse(id, id, id2,
                BigDecimal.TEN));

        when(bundleDiscountRepository.findAll()).thenReturn(Collections.singletonList(bundleDiscount));

        List<BundleDiscountResponse> actualResponse = bundleDiscountService.getAllBundledDiscounts();

        assert bundleDiscountResponses.equals(actualResponse);

        verify(bundleDiscountRepository, times(1)).findAll();
        verifyNoMoreInteractions(bundleDiscountRepository);
        verifyNoInteractions(itemService);
    }

    @Test
    public void testGetBundleDiscountById() {
        BundleDiscount bundleDiscount = Utils.buildBundleDiscount(id, id, id2, BigDecimal.TEN);

        when(bundleDiscountRepository.findById(id)).thenReturn(Optional.of(bundleDiscount));

        assert bundleDiscount.equals(bundleDiscountService.getBundleDiscountById(id));

        verify(bundleDiscountRepository, times(1)).findById(id);
        verifyNoMoreInteractions(bundleDiscountRepository);
        verifyNoInteractions(itemService);
    }

    @Test
    public void testGetBundleDiscountResponseById() {
        BundleDiscount bundleDiscount = Utils.buildBundleDiscount(id, id, id2, BigDecimal.TEN);

        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2,
                BigDecimal.TEN);

        when(bundleDiscountRepository.findById(id)).thenReturn(Optional.of(bundleDiscount));

        BundleDiscountResponse actualResponse = bundleDiscountService.getBundleDiscountResponseById(id);

        assert bundleDiscountResponse.equals(actualResponse);

        verify(bundleDiscountRepository, times(1)).findById(id);
        verifyNoMoreInteractions(bundleDiscountRepository);
        verifyNoInteractions(itemService);
    }

    @Test
    public void testCreateBundleDiscount() {
        Item item = Utils.buildItem(id);
        Item item2 = Utils.buildItem(id2);
        BundleDiscountRequest bundleDiscountRequest = Utils.buildBundleDiscountRequest(id, id2, BigDecimal.TEN);
        BundleDiscount bundleDiscount = Utils.buildBundleDiscount(id, id, id2, BigDecimal.TEN);
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.TEN);

        when(itemService.getItemEntityById(id)).thenReturn(item);
        when(itemService.getItemEntityById(id2)).thenReturn(item2);
        when(bundleDiscountRepository.save(bundleDiscount)).thenReturn(bundleDiscount);

        assert bundleDiscountResponse.equals(bundleDiscountService.createBundleDiscount(bundleDiscountRequest));

        verify(bundleDiscountRepository, times(1)).save(bundleDiscount);
        verify(itemService, times(1)).getItemEntityById(id);
        verify(itemService, times(1)).getItemEntityById(id2);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void testUpdateBundleDiscount() {
        Item item = Utils.buildItem(id);
        Item item2 = Utils.buildItem(id2);
        BundleDiscount bundleDiscount = Utils.buildBundleDiscount(id, id, id2, BigDecimal.TEN);
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.TEN);
        BundleDiscountRequest bundleDiscountRequest = Utils.buildBundleDiscountRequest(id, id2, BigDecimal.TEN);


        when(itemService.getItemEntityById(id)).thenReturn(item);
        when(itemService.getItemEntityById(id2)).thenReturn(item2);
        when(bundleDiscountRepository.save(bundleDiscount)).thenReturn(bundleDiscount);
        when(bundleDiscountRepository.findById(id)).thenReturn(Optional.of(bundleDiscount));

        BundleDiscountResponse actualResponse = bundleDiscountService.updateBundleDiscount(id, bundleDiscountRequest);

        assert bundleDiscountResponse.equals(actualResponse);

        verify(bundleDiscountRepository, times(1)).findById(id);
        verify(bundleDiscountRepository, times(1)).save(bundleDiscount);
        verify(itemService, times(1)).getItemEntityById(id);
        verify(itemService, times(1)).getItemEntityById(id2);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void testPartialUpdateBundleDiscount() {
        BundleDiscount bundleDiscount = Utils.buildBundleDiscount(id, id, id2, BigDecimal.TEN);
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.ONE);
        BundleDiscountPatchRequest bundleDiscountPatchRequest = Utils.buildBundleDiscountPatchRequest(null, null, BigDecimal.ONE);

        when(bundleDiscountRepository.save(bundleDiscount)).thenReturn(bundleDiscount);
        when(bundleDiscountRepository.findById(id)).thenReturn(Optional.of(bundleDiscount));

        BundleDiscountResponse actualResponse = bundleDiscountService.partialUpdateBundleDiscount(id, bundleDiscountPatchRequest);

        assert bundleDiscountResponse.equals(actualResponse);

        verify(bundleDiscountRepository, times(1)).findById(id);
        verify(bundleDiscountRepository, times(1)).save(bundleDiscount);
    }

    @Test
    public void testDeleteBundleDiscount() {
        BundleDiscount bundleDiscount = Utils.buildBundleDiscount(id, id, id2, BigDecimal.TEN);
        when(bundleDiscountRepository.findById(id)).thenReturn(Optional.of(bundleDiscount));

        bundleDiscountService.deleteBundleDiscount(id);

        verify(bundleDiscountRepository, times(1)).findById(id);
        verify(bundleDiscountRepository, times(1)).delete(bundleDiscount);
    }

    @Test
    public void testValidateSameItems() {
        assertThrows(IllegalArgumentException.class, () -> bundleDiscountService.validateSameItems(id, id));
    }
}















