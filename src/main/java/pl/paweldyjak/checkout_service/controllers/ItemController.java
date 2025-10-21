package pl.paweldyjak.checkout_service.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable Long id) {
        logger.info("Received GET request to get item with id: {}", id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemResponse> getAllItems() {
        logger.info("Received GET request to get all items");
        return itemService.getAllItems();
    }

    @GetMapping("/names")
    public List<String> getAllAvailableItemNames() {
        logger.info("Received GET request to get all available item names");
        return itemService.getAllAvailableItemNames();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) {
        logger.info("Received POST request to create item");
        return itemService.createItem(request);
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@Valid @RequestBody ItemRequest itemRequest, @PathVariable Long id) {
        logger.info("Received PUT request to update item with id: {}", id);
        return itemService.updateItem(id, itemRequest);
    }

    @PatchMapping("/{id}")
    public ItemResponse partialUpdateItem(@Valid @RequestBody ItemPatchRequest itemPatchRequest, @PathVariable Long id) {
        logger.info("Received PATCH request to partial update item with id: {}", id);
        return itemService.partialUpdateItem(id, itemPatchRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        logger.info("Received DELETE request to delete item with id: {}", id);
        itemService.deleteItem(id);
    }
}
