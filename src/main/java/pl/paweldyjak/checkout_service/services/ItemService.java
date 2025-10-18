package pl.paweldyjak.checkout_service.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequest;
import pl.paweldyjak.checkout_service.dtos.request.ItemPatchRequest;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemNotFoundException;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.repositories.BundleDiscountRepository;
import pl.paweldyjak.checkout_service.repositories.ItemRepository;

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
                .map(itemMapper::mapToItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long itemId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        return itemMapper.mapToItemResponse(existingItem);

    }

    @Transactional
    public ItemResponse createItem(ItemRequest request) {
        Item newItem = itemMapper.mapToItemEntity(request);
        validatePricesAndRequiredQuantity(newItem);
        Item savedItem = itemRepository.save(newItem);
        return itemMapper.mapToItemResponse(savedItem);
    }

    @Transactional
    public ItemResponse updateItem(Long itemId, ItemRequest itemRequest) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        itemMapper.updateItemEntity(existingItem, itemRequest);
        validatePricesAndRequiredQuantity(existingItem);
        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.mapToItemResponse(updatedItem);
    }

    @Transactional
    public ItemResponse partialUpdateItem(Long itemId, ItemPatchRequest patchRequest) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        boolean priceFieldsChanged = false;

        if (patchRequest.name() != null) {
            existingItem.setName(patchRequest.name());
        }
        if (patchRequest.normalPrice() != null) {
            existingItem.setNormalPrice(patchRequest.normalPrice());
            priceFieldsChanged = true;
        }
        if (patchRequest.requiredQuantity() != null) {
            existingItem.setRequiredQuantity(patchRequest.requiredQuantity());
            priceFieldsChanged = true;
        }
        if (patchRequest.specialPrice() != null) {
            existingItem.setSpecialPrice(patchRequest.specialPrice());
            priceFieldsChanged = true;
        }

        if (priceFieldsChanged) {
            validatePricesAndRequiredQuantity(existingItem);
        }

        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.mapToItemResponse(updatedItem);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        bundleDiscountRepository.deleteDiscountsForItem(itemId);
        itemRepository.deleteById(itemId);
    }

    private void validatePricesAndRequiredQuantity(Item item) {
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

    public List<String> getAllAvailableItemNames() {
        return itemRepository.findAllAvailableItemNames();
    }
}
