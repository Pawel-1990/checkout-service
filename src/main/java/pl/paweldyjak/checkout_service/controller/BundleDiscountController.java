package pl.paweldyjak.checkout_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.service.BundleDiscountService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BundleDiscountController {

    private final BundleDiscountService bundleDiscountService;

    public BundleDiscountController(BundleDiscountService bundleDiscountService) {
        this.bundleDiscountService = bundleDiscountService;
    }

    @GetMapping("/discounts")
    public List<BundleDiscount> getAllDiscounts() {
        return bundleDiscountService.getAllDiscounts();
    }

    @GetMapping("/discounts/{discountId}")
    public BundleDiscount getDiscountById(@PathVariable Long discountId) {
        return bundleDiscountService.getDiscountById(discountId);
    }

    @PatchMapping("/discounts/{discountId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateDiscountById(@PathVariable Long discountId) {
        bundleDiscountService.deactivateDiscountById(discountId);
    }
}
