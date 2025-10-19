package pl.paweldyjak.checkout_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequest;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.services.BundleDiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/bundle-discounts")
public class BundleDiscountController {

    private final BundleDiscountService bundleDiscountService;

    public BundleDiscountController(BundleDiscountService bundleDiscountService) {
        this.bundleDiscountService = bundleDiscountService;
    }

    @GetMapping("/{id}")
    public BundleDiscountResponse getBundleDiscountById(@PathVariable Long id) {
        return bundleDiscountService.getBundleDiscountResponseById(id);
    }

    @GetMapping
    public List<BundleDiscountResponse> getAllBundledDiscounts() {
        return bundleDiscountService.getAllBundledDiscounts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BundleDiscountResponse createBundleDiscount(
            @Valid @RequestBody BundleDiscountRequest request) {
        return bundleDiscountService.createBundleDiscount(request);
    }

    @PutMapping("/{id}")
    public BundleDiscountResponse updateBundleDiscount(
            @PathVariable Long id,
            @Valid @RequestBody BundleDiscountRequest request) {
        return bundleDiscountService.updateBundleDiscount(id, request);
    }

    @PatchMapping("/{id}")
    public BundleDiscountResponse partialUpdateBundleDiscount(
            @PathVariable Long id,
            @Valid @RequestBody BundleDiscountPatchRequest request) {
        return bundleDiscountService.partialUpdateBundleDiscount(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBundleDiscount(@PathVariable Long id) {
        bundleDiscountService.deleteBundleDiscount(id);
    }
}
