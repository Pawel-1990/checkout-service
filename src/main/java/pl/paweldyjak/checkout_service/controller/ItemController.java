package pl.paweldyjak.checkout_service.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/items/{itemId}")
    public Item getItemById(@PathVariable  Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping("/items")
    public Item saveItem(@RequestBody Item item) {
        if (item.getId() != null) {
            // TODO: add custom exceptions
            throw new IllegalArgumentException("Nowy item nie może mieć ustawionego ID");
        }
        return itemService.saveItem(item);
    }

    @PutMapping("/items/{itemId}")
    public Item updateItem(@RequestBody Item item, @PathVariable Long itemId) {
        // TODO: add custom exceptions
        if (item.getId() == null) {
            throw new IllegalArgumentException("Item ID not found");
        }
        if (!itemService.getItemRepository().existsById(item.getId())) {
            throw new IllegalArgumentException("Item with ID " + item.getId() + " does not exist");
        }
        return itemService.saveItem(item);
    }


}
