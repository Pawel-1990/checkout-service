package pl.paweldyjak.checkout_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.dtos.request.AddItemsRequest;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.services.CheckoutService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/checkouts")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping
    List<CheckoutResponse> getAllCheckouts() {
        return checkoutService.getAllCheckouts();
    }

    @GetMapping("/{id}")
    CheckoutResponse getCheckoutById(@PathVariable Long id) {
        return checkoutService.getCheckoutById(id);
    }


    @GetMapping("/{id}/items")
    Map<String, Integer> getItemsByCheckoutId(@PathVariable Long id) {
        return checkoutService.getItemsByCheckoutId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutResponse createCheckout() {
        return checkoutService.createCheckout();
    }

    @PostMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptResponse pay(@PathVariable Long id) {
        return checkoutService.pay(id);
    }

    @PatchMapping("/{id}/add-items")
    public CheckoutResponse addItemsToCheckout(@PathVariable Long id, @Valid @RequestBody List<AddItemsRequest> items) {
        return checkoutService.addItemsToCheckout(id, items);
    }

    @PatchMapping("/{id}/delete-items")
    public CheckoutResponse deleteItemsFromCheckout(@PathVariable Long id, @Valid @RequestBody List<AddItemsRequest> items) {
        return checkoutService.deleteItemsFromCheckout(id, items);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCheckout(@PathVariable Long id) {
        checkoutService.deleteCheckout(id);
    }
}
