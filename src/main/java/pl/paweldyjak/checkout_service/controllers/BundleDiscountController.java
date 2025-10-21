package pl.paweldyjak.checkout_service.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(BundleDiscountController.class);
    private final BundleDiscountService bundleDiscountService;

    public BundleDiscountController(BundleDiscountService bundleDiscountService) {
        this.bundleDiscountService = bundleDiscountService;
    }

    @GetMapping("/{id}")
    public BundleDiscountResponse getBundleDiscountById(@PathVariable Long id) {
        logger.info("Received GET request to get bundle discount with id: {}", id);
        return bundleDiscountService.getBundleDiscountResponseById(id);
    }

    @GetMapping
    public List<BundleDiscountResponse> getAllBundledDiscounts() {
        logger.info("Received GET request to get all bundle discounts");
        return bundleDiscountService.getAllBundledDiscounts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BundleDiscountResponse createBundleDiscount(
            @Valid @RequestBody BundleDiscountRequest request) {
        logger.info("Received POST request to create bundle discount");
        return bundleDiscountService.createBundleDiscount(request);
    }

    @PutMapping("/{id}")
    public BundleDiscountResponse updateBundleDiscount(
            @PathVariable Long id,
            @Valid @RequestBody BundleDiscountRequest request) {
        logger.info("Received PUT request to update bundle discount with id: {}", id);
        return bundleDiscountService.updateBundleDiscount(id, request);
    }

    @PatchMapping("/{id}")
    public BundleDiscountResponse partialUpdateBundleDiscount(
            @PathVariable Long id,
            @Valid @RequestBody BundleDiscountPatchRequest request) {
        logger.info("Received PATCH request to partial update bundle discount with id: {}", id);
        return bundleDiscountService.partialUpdateBundleDiscount(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBundleDiscount(@PathVariable Long id) {
        logger.info("Received DELETE request to delete bundle discount with id: {}", id);
        bundleDiscountService.deleteBundleDiscount(id);
    }
}
