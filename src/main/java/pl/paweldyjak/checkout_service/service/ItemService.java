package pl.paweldyjak.checkout_service.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.paweldyjak.checkout_service.dto.ItemPatchDTO;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemHasActiveDiscountsException;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemNotFoundException;
import pl.paweldyjak.checkout_service.repository.BundleDiscountRepository;
import pl.paweldyjak.checkout_service.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final BundleDiscountRepository bundleDiscountRepository;

    public ItemService(ItemRepository itemRepository, BundleDiscountRepository bundleDiscountRepository) {
        this.itemRepository = itemRepository;
        this.bundleDiscountRepository = bundleDiscountRepository;
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item saveItem(Item item) {
        if (!itemRepository.existsById(item.getId())) {
            throw new ItemNotFoundException(item.getId());
        }
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    public Item patchItem(Long itemId, ItemPatchDTO patchDTO) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (patchDTO.getName() != null) {
            existingItem.setName(patchDTO.getName());
        }
        if (patchDTO.getNormalPrice() != null) {
            existingItem.setNormalPrice(patchDTO.getNormalPrice());
        }
        if (patchDTO.getRequiredQuantity() != null) {
            existingItem.setRequiredQuantity(patchDTO.getRequiredQuantity());
        }
        if (patchDTO.getSpecialPrice() != null) {
            existingItem.setSpecialPrice(patchDTO.getSpecialPrice());
        }

        validateBundleDiscount(existingItem);

        return itemRepository.save(existingItem);
    }



    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        if (bundleDiscountRepository.checkIfActiveDiscountExistsForItem(itemId)) {
            throw new ItemHasActiveDiscountsException(itemId);
        }
        bundleDiscountRepository.deleteInactiveDiscountsForItem(itemId);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public void deactivateDiscountByItemId(Long itemId) {
        bundleDiscountRepository.deactivateDiscountsByItemId(itemId);
    }

    private void validateBundleDiscount(Item item) {
        boolean hasRequiredQuantity = item.getRequiredQuantity() > 0;
        boolean hasSpecialPrice = item.getSpecialPrice() != null && item.getSpecialPrice().compareTo(BigDecimal.ZERO) > 0;

        if (hasRequiredQuantity && !hasSpecialPrice) {
            throw new IllegalArgumentException("Both required_quantity and special_price must be set together for discount");
        }
        if (hasSpecialPrice && !hasRequiredQuantity) {
            throw new IllegalArgumentException("Both required_quantity and special_price must be set together for discount");
        }
    }

}
