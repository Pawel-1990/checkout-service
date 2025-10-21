package pl.paweldyjak.checkout_service.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemInfo;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.services.CheckoutService;

import java.util.List;

@RestController
@RequestMapping("api/checkouts")
public class CheckoutController {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/{id}")
    CheckoutResponse getCheckoutById(@PathVariable Long id) {
        logger.info("Received GET request to get checkout with id: {}", id);
        return checkoutService.getCheckoutById(id);
    }

    @GetMapping("/{id}/receipt")
    ReceiptResponse getReceiptById(@PathVariable Long id) {
        logger.info("Received GET request to get receipt with id: {}", id);
        return checkoutService.getReceiptByCheckoutId(id);
    }

    @GetMapping
    List<CheckoutResponse> getAllCheckouts() {
        logger.info("Received GET request to get all checkouts");
        return checkoutService.getAllCheckouts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutResponse createCheckout() {
        logger.info("Received POST request to create checkout");
        return checkoutService.createCheckout();
    }

    @PostMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptResponse pay(@PathVariable Long id) {
        logger.info("Received POST request to pay checkout with id: {}", id);
        return checkoutService.pay(id);
    }

    @PatchMapping("/{id}/add-items")
    public CheckoutResponse addItemsToCheckout(@PathVariable Long id, @Valid @RequestBody List<CheckoutItemInfo> items) {
        logger.info("Received PATCH request to add items to checkout with id: {}", id);
        return checkoutService.addItemsToCheckout(id, items);
    }

    @PatchMapping("/{id}/delete-items")
    public CheckoutResponse deleteItemsFromCheckout(@PathVariable Long id, @Valid @RequestBody List<CheckoutItemInfo> items) {
        logger.info("Received PATCH request to delete items from checkout with id: {}", id);
        return checkoutService.deleteItemsFromCheckout(id, items);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCheckout(@PathVariable Long id) {
        logger.info("Received DELETE request to delete checkout with id: {}", id);
        checkoutService.deleteCheckout(id);
    }
}
