package pl.paweldyjak.checkout_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.dto.request.ItemRequest;
import pl.paweldyjak.checkout_service.dto.request.ItemPatchRequest;
import pl.paweldyjak.checkout_service.dto.response.ItemResponse;
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
    public List<ItemResponse> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/items/{itemId}")
    public ItemResponse getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping("/items")
    public ItemResponse createItem(@Valid @RequestBody ItemRequest itemRequest) {
        return itemService.createItem(itemRequest);
    }

    @PutMapping("/items/{itemId}")
    public ItemResponse updateItem(@Valid @RequestBody ItemRequest itemRequest, @PathVariable Long itemId) {
        return itemService.updateItem(itemId, itemRequest);
    }

    @PatchMapping("/items/{itemId}")
    public ItemResponse patchItem(@Valid @RequestBody ItemPatchRequest itemPatchRequest, @PathVariable Long itemId) {
        return itemService.patchItem(itemId, itemPatchRequest);
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
