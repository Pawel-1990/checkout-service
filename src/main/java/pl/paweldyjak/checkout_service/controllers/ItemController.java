package pl.paweldyjak.checkout_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequest;
import pl.paweldyjak.checkout_service.dtos.request.ItemPatchRequest;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponse;
import pl.paweldyjak.checkout_service.services.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemResponse> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/names")
    public List<String> getAllAvailableItemNames() {
        return itemService.getAllAvailableItemNames();
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

    @PatchMapping("/{id}")
    public ItemResponse partialUpdateItem(@Valid @RequestBody ItemPatchRequest itemPatchRequest, @PathVariable Long id) {
        return itemService.partialUpdateItem(id, itemPatchRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
