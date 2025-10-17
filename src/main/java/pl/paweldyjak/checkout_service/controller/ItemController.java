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
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) {
        return itemService.createItem(request);
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@Valid @RequestBody ItemRequest itemRequest, @PathVariable Long id) {
        return itemService.updateItem(id, itemRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse patchItem(@Valid @RequestBody ItemPatchRequest itemPatchRequest, @PathVariable Long itemId) {
        return itemService.partialUpdateItem(itemId, itemPatchRequest);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
