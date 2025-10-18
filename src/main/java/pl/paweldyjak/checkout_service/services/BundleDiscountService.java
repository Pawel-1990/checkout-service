package pl.paweldyjak.checkout_service.services;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequest;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.bundle_discount_exceptions.BundleDiscountNotFoundException;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemNotFoundException;
import pl.paweldyjak.checkout_service.mappers.BundleDiscountMapper;
import pl.paweldyjak.checkout_service.repositories.BundleDiscountRepository;
import pl.paweldyjak.checkout_service.repositories.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BundleDiscountService {

    private final BundleDiscountRepository bundleDiscountRepository;
    private final BundleDiscountMapper bundleDiscountMapper;
    private final ItemRepository itemRepository;


    public BundleDiscountService(BundleDiscountRepository bundleDiscountRepository, BundleDiscountMapper bundleDiscountMapper, ItemRepository itemRepository) {
        this.bundleDiscountRepository = bundleDiscountRepository;
        this.bundleDiscountMapper = bundleDiscountMapper;
        this.itemRepository = itemRepository;
    }

    public List<BundleDiscountResponse> getAllBundledDiscounts() {
        return bundleDiscountRepository.findAll().stream()
                .map(bundleDiscountMapper::mapToBundleDiscountResponse)
                .collect(Collectors.toList());

    }

    public BundleDiscountResponse getBundleDiscountById(Long id) {
        BundleDiscount discount = findBundleDiscountById(id);
        return bundleDiscountMapper.mapToBundleDiscountResponse(discount);

    }
    @Transactional
    public BundleDiscountResponse createBundleDiscount(@Valid BundleDiscountRequest request) {

        validateSameItems(request.firstItemId(), request.secondItemId());

        Item firstItem = findItemById(request.firstItemId());
        Item secondItem = findItemById(request.secondItemId());

        BundleDiscount newBundleDiscount = bundleDiscountMapper.mapToBundleDiscountEntity(
                request, firstItem, secondItem);

        BundleDiscount savedBundleDiscount = bundleDiscountRepository.save(newBundleDiscount);
        return bundleDiscountMapper.mapToBundleDiscountResponse(savedBundleDiscount);
    }

    @Transactional
    public BundleDiscountResponse updateBundleDiscount(Long id, @Valid BundleDiscountRequest request) {
        BundleDiscount existingBundleDiscount = findBundleDiscountById(id);

        validateSameItems(request.firstItemId(), request.secondItemId());

        Item firstItem = findItemById(request.firstItemId());
        Item secondItem = findItemById(request.secondItemId());

        existingBundleDiscount.setDiscountAmount(request.discountAmount());
        existingBundleDiscount.setFirstItem(firstItem);
        existingBundleDiscount.setSecondItem(secondItem);

        BundleDiscount savedBundleDiscount = bundleDiscountRepository.save(existingBundleDiscount);
        return bundleDiscountMapper.mapToBundleDiscountResponse(savedBundleDiscount);
    }

    @Transactional
    public BundleDiscountResponse partialUpdateBundleDiscount(Long id, @Valid BundleDiscountPatchRequest request) {
        BundleDiscount existingBundleDiscount = findBundleDiscountById(id);

        Long finalFirstItemId = request.firstItemId() != null
                ? request.firstItemId()
                : existingBundleDiscount.getFirstItem().getId();

        Long finalSecondItemId = request.secondItemId() != null
                ? request.secondItemId()
                : existingBundleDiscount.getSecondItem().getId();

        validateSameItems(finalFirstItemId, finalSecondItemId);

        if (request.firstItemId() != null) {
            Item firstItem = findItemById(finalFirstItemId);
            existingBundleDiscount.setFirstItem(firstItem);
        }

        if (request.secondItemId() != null) {
            Item secondItem = findItemById(finalSecondItemId);
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
        BundleDiscount bundleDiscount = findBundleDiscountById(id);
        bundleDiscountRepository.delete(bundleDiscount);
    }

    private void validateSameItems(Long firstItemId, Long secondItemId) {
        if (firstItemId != null && firstItemId.equals(secondItemId)) {
            throw new IllegalArgumentException("Bundle discount cannot contain the same item twice");
        }
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private BundleDiscount findBundleDiscountById(Long id) {
        return bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new BundleDiscountNotFoundException(id));
    }


}
