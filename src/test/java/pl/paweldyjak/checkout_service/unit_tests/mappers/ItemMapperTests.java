package pl.paweldyjak.checkout_service.unit_tests.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.unit_tests.utils.Utils;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequestDto;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponseDto;
import pl.paweldyjak.checkout_service.entities.Item;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTests {
    private ItemMapper itemMapper;

    @BeforeEach
    void setup() {
        itemMapper = new ItemMapper();
    }

    @Test
    public void testMapToCheckoutResponse() {
        Item item = Utils.buildItem(null);
        item.setId(1L);
        ItemResponseDto itemResponseDto = Utils.buildItemResponse(1L);

        assert itemResponseDto.equals(itemMapper.mapToItemResponse(item));
    }

    @Test
    public void testMapToItemEntity() {
        ItemRequestDto itemRequestDto = Utils.buildItemRequest();
        Item item = Utils.buildItem(null);
        Item item2 = itemMapper.mapToItemEntity(itemRequestDto);
        assert item.equals(item2);
    }

    @Test
    public void testUpdateItemEntity() {
        Item item = Utils.buildItem(null);
        item.setSpecialPrice(BigDecimal.valueOf(100));
        ItemRequestDto itemRequestDto = Utils.buildItemRequest();

        Item expectedItem = Utils.buildItem(null);

        assert expectedItem.equals(itemMapper.updateItemEntity(item, itemRequestDto));
    }
}
