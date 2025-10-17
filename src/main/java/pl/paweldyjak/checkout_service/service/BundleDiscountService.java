package pl.paweldyjak.checkout_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dto.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.exceptions.bundle_discount_exceptions.BundleDiscountNotFoundException;
import pl.paweldyjak.checkout_service.mappers.BundleDiscountMapper;
import pl.paweldyjak.checkout_service.repository.BundleDiscountRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BundleDiscountService {

    private final BundleDiscountRepository bundleDiscountRepository;
    private final BundleDiscountMapper bundleDiscountMapper;


    public BundleDiscountService(BundleDiscountRepository bundleDiscountRepository, BundleDiscountMapper bundleDiscountMapper) {
        this.bundleDiscountRepository = bundleDiscountRepository;
        this.bundleDiscountMapper = bundleDiscountMapper;
    }

    public List<BundleDiscountResponse> getAllDiscounts() {
        return bundleDiscountRepository.findAll().stream()
                .map(bundleDiscountMapper::convertToBundleDiscountResponse)
                .collect(Collectors.toList());

    }

    public BundleDiscountResponse getDiscountById(Long id) {
        BundleDiscount discount = bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new BundleDiscountNotFoundException(id));
        return bundleDiscountMapper.convertToBundleDiscountResponse(discount);

    }

    @Transactional
    public void deactivateDiscountById(Long discountId) {
        if (!bundleDiscountRepository.existsById(discountId)) {
            throw new BundleDiscountNotFoundException(discountId);
        }
        bundleDiscountRepository.deactivateDiscountsById(discountId);
    }
}
