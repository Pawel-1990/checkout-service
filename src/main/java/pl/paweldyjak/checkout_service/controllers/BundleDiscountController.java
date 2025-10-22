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
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequest;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.services.BundleDiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/bundle-discounts")
@Tag(name = "Bundle Discounts", description = "Endpoints for managing bundle discount rules between items")
public class BundleDiscountController {
    private static final Logger logger = LoggerFactory.getLogger(BundleDiscountController.class);
    private final BundleDiscountService bundleDiscountService;

    public BundleDiscountController(BundleDiscountService bundleDiscountService) {
        this.bundleDiscountService = bundleDiscountService;
    }


    @Operation(
            summary = "Get bundle discount by ID",
            description = "Retrieve details of a specific bundle discount rule by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bundle discount found",
                    content = @Content(schema = @Schema(implementation = BundleDiscountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bundle discount not found")
    })
    @GetMapping("/{id}")
    public BundleDiscountResponse getBundleDiscountById(@Parameter(description = "Unique ID of the bundle discount", example = "1") @PathVariable Long id) {
        logger.info("Received GET request to get bundle discount with id: {}", id);
        return bundleDiscountService.getBundleDiscountResponseById(id);
    }


    @Operation(
            summary = "Get all bundle discounts",
            description = "Retrieve a list of all bundle discount rules currently available."
    )
    @ApiResponse(responseCode = "200", description = "List of all bundle discounts",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BundleDiscountResponse.class))))
    @GetMapping
    public List<BundleDiscountResponse> getAllBundledDiscounts() {
        logger.info("Received GET request to get all bundle discounts");
        return bundleDiscountService.getAllBundledDiscounts();
    }


    @Operation(
            summary = "Create a new bundle discount",
            description = "Create a new rule defining a discount when certain items are purchased together."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bundle discount created successfully",
                    content = @Content(schema = @Schema(implementation = BundleDiscountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BundleDiscountResponse createBundleDiscount(
            @Valid @RequestBody BundleDiscountRequest request) {
        logger.info("Received POST request to create bundle discount");
        return bundleDiscountService.createBundleDiscount(request);
    }


    @Operation(
            summary = "Update an existing bundle discount",
            description = "Fully update a bundle discount rule with new data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bundle discount updated successfully"),
            @ApiResponse(responseCode = "404", description = "Bundle discount not found")
    })
    @PutMapping("/{id}")
    public BundleDiscountResponse updateBundleDiscount(
            @Parameter(description = "Unique ID of the bundle discount", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BundleDiscountRequest request) {
        logger.info("Received PUT request to update bundle discount with id: {}", id);
        return bundleDiscountService.updateBundleDiscount(id, request);
    }


    @Operation(
            summary = "Partially update a bundle discount",
            description = "Apply partial modifications to an existing bundle discount rule."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bundle discount partially updated"),
            @ApiResponse(responseCode = "404", description = "Bundle discount not found")
    })
    @PatchMapping("/{id}")
    public BundleDiscountResponse partialUpdateBundleDiscount(
            @Parameter(description = "Unique ID of the bundle discount", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BundleDiscountPatchRequest request) {
        logger.info("Received PATCH request to partial update bundle discount with id: {}", id);
        return bundleDiscountService.partialUpdateBundleDiscount(id, request);
    }


    @Operation(
            summary = "Delete a bundle discount",
            description = "Remove a specific bundle discount rule from the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bundle discount deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Bundle discount not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBundleDiscount(@Parameter(description = "Unique ID of the bundle discount", example = "1") @PathVariable Long id) {
        logger.info("Received DELETE request to delete bundle discount with id: {}", id);
        bundleDiscountService.deleteBundleDiscount(id);
    }
}
