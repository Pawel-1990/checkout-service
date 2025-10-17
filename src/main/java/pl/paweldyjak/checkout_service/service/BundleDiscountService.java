package pl.paweldyjak.checkout_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;
import pl.paweldyjak.checkout_service.exceptions.bundle_discount_exceptions.BundleDiscountNotFoundException;
import pl.paweldyjak.checkout_service.repository.BundleDiscountRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BundleDiscountService {

    private final BundleDiscountRepository bundleDiscountRepository;

    public BundleDiscountService(BundleDiscountRepository bundleDiscountRepository) {
        this.bundleDiscountRepository = bundleDiscountRepository;
    }

    public List<BundleDiscount> getAllDiscounts() {
        return bundleDiscountRepository.findAll();
    }

    public BundleDiscount getDiscountById(Long id) {
        return bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new BundleDiscountNotFoundException(id));
    }

    @Transactional
    public void deactivateDiscountById(Long discountId) {
        if (!bundleDiscountRepository.existsById(discountId)) {
            throw new BundleDiscountNotFoundException(discountId);
        }
        bundleDiscountRepository.deactivateDiscountsById(discountId);
    }
}
