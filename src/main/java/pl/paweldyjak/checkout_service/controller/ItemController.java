package pl.paweldyjak.checkout_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.dto.ItemPatchDTO;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemAlreadyHasIdException;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemIdMismatchException;
import pl.paweldyjak.checkout_service.service.ItemService;

import java.util.List;
import java.util.Objects;

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
    public Item getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping("/items")
    public Item saveItem(@RequestBody Item item) {
        if (item.getId() != null) {
            throw new ItemAlreadyHasIdException(item.getId());
        }
        return itemService.saveItem(item);
    }

    @PutMapping("/items/{itemId}")
    public Item updateItem(@RequestBody Item item, @PathVariable Long itemId) {
        if (!Objects.equals(item.getId(), itemId)) {
            throw new ItemIdMismatchException(itemId, item.getId());
        }
        return itemService.saveItem(item);
    }

    @PatchMapping("/items/{itemId}")
    public Item patchItem(@Valid @RequestBody ItemPatchDTO patchDTO, @PathVariable Long itemId) {
        return itemService.patchItem(itemId, patchDTO);
    }

    @PatchMapping("/items/{itemId}/discounts/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateDiscountByItemId(@PathVariable Long itemId) {
        itemService.deactivateDiscountByItemId(itemId);
    }

    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
