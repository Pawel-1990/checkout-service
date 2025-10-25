package pl.paweldyjak.checkout_service.services;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequestDto;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequestDto;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponseDto;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.bundle_discount_exceptions.BundleDiscountNotFoundException;
import pl.paweldyjak.checkout_service.mappers.BundleDiscountMapper;
import pl.paweldyjak.checkout_service.repositories.BundleDiscountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BundleDiscountService {

    private final BundleDiscountRepository bundleDiscountRepository;
    private final BundleDiscountMapper bundleDiscountMapper;
    private final ItemService itemService;


    public BundleDiscountService(BundleDiscountRepository bundleDiscountRepository, BundleDiscountMapper bundleDiscountMapper,
                                 ItemService itemService) {
        this.bundleDiscountRepository = bundleDiscountRepository;
        this.bundleDiscountMapper = bundleDiscountMapper;
        this.itemService = itemService;
    }

    public List<BundleDiscountResponseDto> getAllBundledDiscounts() {
        return bundleDiscountRepository.findAll().stream()
                .map(bundleDiscountMapper::mapToBundleDiscountResponse)
                .collect(Collectors.toList());

    }

    public BundleDiscount getBundleDiscountById(Long id) {
        return bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new BundleDiscountNotFoundException(id));
    }

    public BundleDiscountResponseDto getBundleDiscountResponseById(Long id) {
        BundleDiscount discount = getBundleDiscountById(id);
        return bundleDiscountMapper.mapToBundleDiscountResponse(discount);
    }

    public BigDecimal getSumDiscountsForItemNames(List<String> names) {
        return bundleDiscountRepository.getSumDiscountsForItemNames(names);
    }

    @Transactional
    public BundleDiscountResponseDto createBundleDiscount(@Valid BundleDiscountRequestDto request) {
        validateSameItems(request.firstItemId(), request.secondItemId());

        Item firstItem = itemService.getItemEntityById(request.firstItemId());
        Item secondItem = itemService.getItemEntityById(request.secondItemId());

        BundleDiscount savedBundleDiscount = bundleDiscountRepository.save(bundleDiscountMapper.mapToBundleDiscountEntity(
                request, firstItem, secondItem));
        return bundleDiscountMapper.mapToBundleDiscountResponse(savedBundleDiscount);
    }

    @Transactional
    public BundleDiscountResponseDto updateBundleDiscount(Long id, @Valid BundleDiscountRequestDto request) {
        BundleDiscount existingBundleDiscount = getBundleDiscountById(id);

        validateSameItems(request.firstItemId(), request.secondItemId());

        Item firstItem = itemService.getItemEntityById(request.firstItemId());
        Item secondItem = itemService.getItemEntityById(request.secondItemId());

        existingBundleDiscount.setDiscountAmount(request.discountAmount());
        existingBundleDiscount.setFirstItem(firstItem);
        existingBundleDiscount.setSecondItem(secondItem);

        BundleDiscount savedBundleDiscount = bundleDiscountRepository.save(existingBundleDiscount);
        return bundleDiscountMapper.mapToBundleDiscountResponse(savedBundleDiscount);
    }

    @Transactional
    public BundleDiscountResponseDto partialUpdateBundleDiscount(Long id, @Valid BundleDiscountPatchRequestDto request) {
        BundleDiscount existingBundleDiscount = getBundleDiscountById(id);

        Long finalFirstItemId = request.firstItemId() != null
                ? request.firstItemId()
                : existingBundleDiscount.getFirstItem().getId();

        Long finalSecondItemId = request.secondItemId() != null
                ? request.secondItemId()
                : existingBundleDiscount.getSecondItem().getId();

        validateSameItems(finalFirstItemId, finalSecondItemId);

        if (request.firstItemId() != null) {
            Item firstItem = itemService.getItemEntityById(finalFirstItemId);
            existingBundleDiscount.setFirstItem(firstItem);
        }

        if (request.secondItemId() != null) {
            Item secondItem = itemService.getItemEntityById(finalSecondItemId);
            existingBundleDiscount.setSecondItem(secondItem);
        }

        if (request.discountAmount() != null) {
            existingBundleDiscount.setDiscountAmount(request.discountAmount());
        }

        BundleDiscount savedBundleDiscount = bundleDiscountRepository.save(existingBundleDiscount);
        return bundleDiscountMapper.mapToBundleDiscountResponse(savedBundleDiscount);
    }

    @Transactional
    public void deleteBundleDiscount(Long id) {
        BundleDiscount bundleDiscount = getBundleDiscountById(id);
        bundleDiscountRepository.delete(bundleDiscount);
    }

    public void validateSameItems(Long firstItemId, Long secondItemId) {
        if (firstItemId != null && firstItemId.equals(secondItemId)) {
            throw new IllegalArgumentException("Bundle discount cannot contain the same item twice");
        }
    }
}
