package pl.paweldyjak.checkout_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Items", description = "Endpoints for managing store items and prices")
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @Operation(
            summary = "Get item by ID",
            description = "Retrieve detailed information about a specific item, including name and price."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item found",
                    content = @Content(schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping("/{id}")
    public ItemResponse getItemById(@Parameter(description = "Unique ID of the item", example = "1") @PathVariable Long id) {
        logger.info("Received GET request to get item with id: {}", id);
        return itemService.getItemById(id);
    }


    @Operation(
            summary = "Get all items",
            description = "Retrieve a list of all items available in the store."
    )
    @ApiResponse(responseCode = "200", description = "List of all items",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ItemResponse.class))))
    @GetMapping
    public List<ItemResponse> getAllItems() {
        logger.info("Received GET request to get all items");
        return itemService.getAllItems();
    }


    @Operation(
            summary = "Get all available item names",
            description = "Retrieve a list of names of all items currently available for sale."
    )
    @ApiResponse(responseCode = "200", description = "List of item names",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @GetMapping("/names")
    public List<String> getAllAvailableItemNames() {
        logger.info("Received GET request to get all available item names");
        return itemService.getAllAvailableItemNames();
    }


    @Operation(
            summary = "Create a new item",
            description = "Add a new item to the store's catalog."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created successfully",
                    content = @Content(schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) {
        logger.info("Received POST request to create item");
        return itemService.createItem(request);
    }


    @Operation(
            summary = "Update an existing item",
            description = "Fully update item information such as name or price."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PutMapping("/{id}")
    public ItemResponse updateItem(@Valid @RequestBody ItemRequest itemRequest,
                                   @Parameter(description = "Unique ID of the item", example = "1") @PathVariable Long id) {
        logger.info("Received PUT request to update item with id: {}", id);
        return itemService.updateItem(id, itemRequest);
    }


    @Operation(
            summary = "Partially update an item",
            description = "Update specific fields of an existing item."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item partially updated"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PatchMapping("/{id}")
    public ItemResponse partialUpdateItem(@Valid @RequestBody ItemPatchRequest itemPatchRequest, @Parameter(description = "Unique ID of the item",
            example = "1") @PathVariable Long id) {
        logger.info("Received PATCH request to partial update item with id: {}", id);
        return itemService.partialUpdateItem(id, itemPatchRequest);
    }


    @Operation(
            summary = "Delete an item",
            description = "Remove an existing item from the store."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@Parameter(description = "Unique ID of the item", example = "1") @PathVariable Long id) {
        logger.info("Received DELETE request to delete item with id: {}", id);
        itemService.deleteItem(id);
    }
}
