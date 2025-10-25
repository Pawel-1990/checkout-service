package pl.paweldyjak.checkout_service.unit_tests.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequestDto;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponseDto;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.repositories.ItemRepository;
import pl.paweldyjak.checkout_service.services.ItemService;
import pl.paweldyjak.checkout_service.unit_tests.utils.Utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {
    private final long id = 1L;
    private ItemService itemService;
    private ItemMapper itemMapper;

    @Mock
    private ItemRepository itemRepository;


    @BeforeEach
    void setup() {
        itemMapper = new ItemMapper();
        itemService = new ItemService(itemRepository, itemMapper);
    }

    @Test
    public void testGetAllItems() {
        Item item = Utils.buildItem(id);
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));

        List<ItemResponseDto> expectedResponse = Collections.singletonList(Utils.buildItemResponse(id));
        List<ItemResponseDto> actualResponse = itemService.getAllItems();

        assert expectedResponse.equals(actualResponse);

        verify(itemRepository, times(1)).findAll();
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testGetAllItemsEntities() {
        Item item = Utils.buildItem(id);
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));

        assert Collections.singletonList(item).equals(itemService.getAllItemsEntities());

        verify(itemRepository, times(1)).findAll();
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testGetItemById() {
        Item item = Utils.buildItem(id);
        ItemResponseDto expectedResponse = Utils.buildItemResponse(id);
        when(itemRepository.findById(id)).thenReturn(java.util.Optional.of(item));

        assert expectedResponse.equals(itemService.getItemById(id));

        verify(itemRepository, times(1)).findById(id);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testCreateItem() {
        ItemRequestDto itemRequestDto = Utils.buildItemRequest();
        Item item = Utils.buildItem(id);
        when(itemRepository.save(any())).thenReturn(item);
        ItemResponseDto expectedResponse = Utils.buildItemResponse(id);

        assert expectedResponse.equals(itemService.createItem(itemRequestDto));

        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testUpdateItem() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("Apple")
                .normalPrice(BigDecimal.valueOf(50))
                .requiredQuantity(3)
                .specialPrice(BigDecimal.valueOf(5))
                .build();
        Item item = Utils.buildItem(id);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemResponseDto expectedResponse = ItemResponseDto.builder()
                .id(id)
                .name("Apple")
                .normalPrice(BigDecimal.valueOf(50))
                .requiredQuantity(3)
                .specialPrice(BigDecimal.valueOf(5))
                .build();

        ItemResponseDto actualResponse = itemService.updateItem(id, itemRequestDto);
        assert expectedResponse.equals(actualResponse);

        verify(itemRepository, times(1)).findById(id);
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testPartialUpdateItem() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .specialPrice(BigDecimal.valueOf(5))
                .build();
        Item item = Utils.buildItem(id);
        Item updatedItem = Utils.buildItem(id);
        updatedItem.setSpecialPrice(BigDecimal.valueOf(5));

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        ItemResponseDto expectedResponse = ItemResponseDto.builder()
                .id(id)
                .name("Apple")
                .normalPrice(BigDecimal.valueOf(50))
                .requiredQuantity(3)
                .specialPrice(BigDecimal.valueOf(5))
                .build();
        when(itemRepository.save(any())).thenReturn(updatedItem);

        ItemResponseDto actualResponse = itemService.partialUpdateItem(id, itemRequestDto);

        assert expectedResponse.equals(actualResponse);

        verify(itemRepository, times(1)).findById(id);
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testDeleteItem() {
        when(itemRepository.existsById(id)).thenReturn(true);
        itemService.deleteItem(id);

        verify(itemRepository, times(1)).existsById(id);
        verify(itemRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(itemRepository);
    }

    @ParameterizedTest
    @MethodSource("provideItemsForValidatePricesAndRequiredQuantity")
    public void testValidatePricesAndRequiredQuantity(Item item) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> itemService.validatePricesAndRequiredQuantity(item));
    }

    static Stream<Arguments> provideItemsForValidatePricesAndRequiredQuantity() {
        return Stream.of(
                // "Normal price must be set and greater than 0"
                Arguments.of(new Item("Apple", BigDecimal.ZERO, 5, BigDecimal.ONE)),
                // "Both required_quantity and special_price must be set together or both null"
                Arguments.of(new Item("Apple", BigDecimal.TEN, 1, BigDecimal.ONE)),
                // "Special price must be lower than the normal price"
                Arguments.of(new Item("Apple", BigDecimal.ONE, 3, BigDecimal.TEN)));
    }

    @Test
    public void testGetItemEntityById() {
        Item item = Utils.buildItem(id);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        assert item.equals(itemService.getItemEntityById(id));

        verify(itemRepository, times(1)).findById(id);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testGetAllAvailableItems() {
        when(itemRepository.findAllAvailableItemNames()).thenReturn(List.of("Apple"));

        assert List.of("Apple").equals(itemService.getAllAvailableItemNames());
    }
}
