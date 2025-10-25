package pl.paweldyjak.checkout_service.unit_tests.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.mappers.BundleDiscountMapper;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.unit_tests.utils.Utils;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequestDto;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponseDto;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Item;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class BundleDiscountMapperTests {

    private BundleDiscountMapper bundleDiscountMapper;
    private ItemMapper itemMapper;

    @BeforeEach
    void setup() {
        itemMapper = new ItemMapper();
        bundleDiscountMapper = new BundleDiscountMapper(itemMapper);
    }

    @Test
    public void testMapToBundleDiscountResponse() {
        BundleDiscount bundleDiscount = new BundleDiscount();
        bundleDiscount.setId(1L);
        bundleDiscount.setFirstItem(Utils.buildItem(1L));
        bundleDiscount.setSecondItem(Utils.buildItem(2L));
        bundleDiscount.setDiscountAmount(BigDecimal.valueOf(10));

        BundleDiscountResponseDto expectedResponse = BundleDiscountResponseDto.builder()
                .id(1L)
                .firstItem(itemMapper.mapToItemResponse(bundleDiscount.getFirstItem()))
                .secondItem(itemMapper.mapToItemResponse(bundleDiscount.getSecondItem()))
                .discountAmount(bundleDiscount.getDiscountAmount())
                .build();

        BundleDiscountResponseDto actualResponse = bundleDiscountMapper.mapToBundleDiscountResponse(bundleDiscount);

        assert expectedResponse.equals(actualResponse);
    }

    @Test
    public void testMapToBundleDiscountEntity() {
        BundleDiscountRequestDto bundleDiscountRequestDto = new BundleDiscountRequestDto(1L, 2L, BigDecimal.valueOf(10));
        Item firstItem = Utils.buildItem(1L);
        Item secondItem = Utils.buildItem(2L);

        BundleDiscount expectedBundleDiscount = new BundleDiscount();
        expectedBundleDiscount.setFirstItem(firstItem);
        expectedBundleDiscount.setSecondItem(secondItem);
        expectedBundleDiscount.setDiscountAmount(BigDecimal.valueOf(10));

        BundleDiscount actualBundleDiscount = bundleDiscountMapper.mapToBundleDiscountEntity(bundleDiscountRequestDto, firstItem, secondItem);

        assert expectedBundleDiscount.equals(actualBundleDiscount);
    }
}
