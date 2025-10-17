package pl.paweldyjak.checkout_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dto.request.ItemRequest;
import pl.paweldyjak.checkout_service.dto.request.ItemPatchRequest;
import pl.paweldyjak.checkout_service.dto.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemHasActiveDiscountsException;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemNotFoundException;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.repository.BundleDiscountRepository;
import pl.paweldyjak.checkout_service.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final BundleDiscountRepository bundleDiscountRepository;
    private final ItemMapper itemMapper;


    public ItemService(ItemRepository itemRepository, BundleDiscountRepository bundleDiscountRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.bundleDiscountRepository = bundleDiscountRepository;
        this.itemMapper = itemMapper;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long itemId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        return itemMapper.toItemResponse(existingItem);

    }

    @Transactional
    public ItemResponse createItem(ItemRequest request) {
        Item newItem = itemMapper.toEntity(request);
        validateBundleDiscount(newItem);
        Item savedItem = itemRepository.save(newItem);
        return itemMapper.toItemResponse(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(Long itemId, ItemRequest itemRequest) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        itemMapper.updateEntity(existingItem, itemRequest);
        validateBundleDiscount(existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toItemResponse(updatedItem);
    }

    @Transactional
    public ItemResponse patchItem(Long itemId, ItemPatchRequest patchRequest) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        boolean priceFieldsChanged = false;

        if (patchRequest.getName() != null) {
            existingItem.setName(patchRequest.getName());
        }
        if (patchRequest.getNormalPrice() != null) {
            existingItem.setNormalPrice(patchRequest.getNormalPrice());
            priceFieldsChanged = true;
        }
        if (patchRequest.getRequiredQuantity() != null) {
            existingItem.setRequiredQuantity(patchRequest.getRequiredQuantity());
            priceFieldsChanged = true;
        }
        if (patchRequest.getSpecialPrice() != null) {
            existingItem.setSpecialPrice(patchRequest.getSpecialPrice());
            priceFieldsChanged = true;
        }

        if (priceFieldsChanged) {
            validateBundleDiscount(existingItem);
        }

        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toItemResponse(updatedItem);
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
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        bundleDiscountRepository.deactivateDiscountsByItemId(itemId);
    }

    private void validateBundleDiscount(Item item) {
        if (item.getNormalPrice() == null || item.getNormalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Normal price must be set and greater than 0");
        }

        boolean hasRequiredQuantity = item.getRequiredQuantity() != null && item.getRequiredQuantity() >= 2;
        boolean hasSpecialPrice = item.getSpecialPrice() != null && item.getSpecialPrice().compareTo(BigDecimal.ZERO) > 0;

        if (hasRequiredQuantity != hasSpecialPrice) {
            throw new IllegalArgumentException("Both required_quantity and special_price must be set together or both null");
        }

        if (hasSpecialPrice && item.getSpecialPrice().compareTo(item.getNormalPrice()) >= 0) {
            throw new IllegalArgumentException("Special price must be lower than normal price");
        }
    }

}
