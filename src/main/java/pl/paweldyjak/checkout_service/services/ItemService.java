package pl.paweldyjak.checkout_service.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequest;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.exceptions.item_exceptions.ItemNotFoundException;
import pl.paweldyjak.checkout_service.mappers.ItemMapper;
import pl.paweldyjak.checkout_service.repositories.ItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemMapper::mapToItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Item> findAllItemsByNameIn(List<String> names) {
        return itemRepository.findAllByNameIn(names);
    }

    @Transactional(readOnly = true)
    public List<Item> getAllItemsEntities() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long itemId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        return itemMapper.mapToItemResponse(existingItem);
    }

    @Transactional(readOnly = true)
    public Item getItemEntityById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Transactional(readOnly = true)
    public List<String> getAllAvailableItemNames() {
        return itemRepository.findAllAvailableItemNames();
    }

    public ItemResponse createItem(ItemRequest request) {
        Item newItem = itemMapper.mapToItemEntity(request);
        validatePricesAndRequiredQuantity(newItem);
        Item savedItem = itemRepository.save(newItem);
        return itemMapper.mapToItemResponse(savedItem);
    }

    public ItemResponse updateItem(Long itemId, ItemRequest itemRequest) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        existingItem = itemMapper.updateItemEntity(existingItem, itemRequest);
        validatePricesAndRequiredQuantity(existingItem);
        Item updatedItem = itemRepository.save(existingItem);

        return itemMapper.mapToItemResponse(updatedItem);
    }

    public ItemResponse partialUpdateItem(Long itemId, ItemRequest patchRequest) {
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

    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        itemRepository.deleteById(itemId);
    }

    public void validatePricesAndRequiredQuantity(Item item) {
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
