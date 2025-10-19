package pl.paweldyjak.checkout_service.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemNotFoundException;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.repositories.ItemRepository;
import pl.paweldyjak.checkout_service.services.ItemService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemExceptionsTests {
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemMapper itemMapper;

    @BeforeEach
    void setup() {
        itemService = new ItemService(itemRepository, itemMapper);
    }

    @Test
    public void testItemNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(1L));
    }
}
