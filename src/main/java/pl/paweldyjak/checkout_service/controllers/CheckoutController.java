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
import pl.paweldyjak.checkout_service.dtos.CheckoutItem;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.services.CheckoutService;

import java.util.List;

@RestController
@RequestMapping("api/checkouts")
@Tag(name = "Checkouts", description = "Endpoints for managing shopping sessions and payments")
public class CheckoutController {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }


    @Operation(
            summary = "Get checkout by ID",
            description = "Retrieve the current state of a checkout session, including scanned items and totals."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checkout found",
                    content = @Content(schema = @Schema(implementation = CheckoutResponse.class))),
            @ApiResponse(responseCode = "404", description = "Checkout not found")
    })
    @GetMapping("/{id}")
    CheckoutResponse getCheckoutById(@Parameter(description = "Unique ID of the checkout", example = "1") @PathVariable Long id) {
        logger.info("Received GET request to get checkout with id: {}", id);
        return checkoutService.getCheckoutById(id);
    }


    @Operation(
            summary = "Get receipt by checkout ID",
            description = "Return a receipt containing all purchased items and total price after payment."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Receipt retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ReceiptResponse.class))),
            @ApiResponse(responseCode = "404", description = "Checkout not found or unpaid")
    })
    @GetMapping("/{id}/receipt")
    ReceiptResponse getReceiptById(@Parameter(description = "Unique ID of the checkout", example = "1") @PathVariable Long id) {
        logger.info("Received GET request to get receipt with id: {}", id);
        return checkoutService.getReceiptByCheckoutId(id);
    }


    @Operation(
            summary = "Get all checkouts",
            description = "Retrieve a list of all checkout sessions."
    )
    @ApiResponse(responseCode = "200", description = "List of all checkouts",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CheckoutResponse.class))))
    @GetMapping
    List<CheckoutResponse> getAllCheckouts() {
        logger.info("Received GET request to get all checkouts");
        return checkoutService.getAllCheckouts();
    }


    @Operation(
            summary = "Create a new checkout session",
            description = "Initialize a new checkout session with an empty list of items."
    )
    @ApiResponse(responseCode = "201", description = "Checkout created successfully",
            content = @Content(schema = @Schema(implementation = CheckoutResponse.class)))
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutResponse createCheckout() {
        logger.info("Received POST request to create checkout");
        return checkoutService.createCheckout();
    }


    @Operation(
            summary = "Pay for a checkout",
            description = "Finalize the checkout process, calculate discounts, and generate a receipt."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment successful and receipt generated",
                    content = @Content(schema = @Schema(implementation = ReceiptResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid state or insufficient data for payment")
    })
    @PostMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptResponse pay(@Parameter(description = "Unique ID of the checkout", example = "1") @PathVariable Long id) {
        logger.info("Received POST request to pay checkout with id: {}", id);
        return checkoutService.pay(id);
    }


    @Operation(
            summary = "Add items to checkout",
            description = "Add one or more items to an existing checkout session."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items added successfully",
                    content = @Content(schema = @Schema(implementation = CheckoutResponse.class))),
            @ApiResponse(responseCode = "404", description = "Checkout not found or item unavailable")
    })
    @PatchMapping("/{id}/add-items")
    public CheckoutResponse addItemsToCheckout(@Parameter(description = "Unique ID of the checkout", example = "1") @PathVariable Long id,
                                               @Valid @RequestBody List<CheckoutItem> items) {
        logger.info("Received PATCH request to add items to checkout with id: {}", id);
        return checkoutService.updateCheckoutItemsAndPrices(id, items);
    }


    @Operation(
            summary = "Delete items from checkout",
            description = "Remove one or more items from an existing checkout session."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items removed successfully",
                    content = @Content(schema = @Schema(implementation = CheckoutResponse.class))),
            @ApiResponse(responseCode = "404", description = "Checkout not found or item not found in checkout")
    })
    @PatchMapping("/{id}/delete-items")
    public CheckoutResponse deleteItemsFromCheckout(@Parameter(description = "Unique ID of the checkout", example = "1") @PathVariable Long id,
                                                    @Valid @RequestBody List<CheckoutItem> items) {
        logger.info("Received PATCH request to delete items from checkout with id: {}", id);
        return checkoutService.deleteItemsFromCheckout(id, items);
    }


    @Operation(
            summary = "Delete a checkout session",
            description = "Remove an existing checkout session and all associated data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Checkout deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Checkout not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCheckout(@Parameter(description = "Unique ID of the checkout", example = "1") @PathVariable Long id) {
        logger.info("Received DELETE request to delete checkout with id: {}", id);
        checkoutService.deleteCheckout(id);
    }
}
