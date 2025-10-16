package pl.paweldyjak.checkout_service.service;

import org.springframework.stereotype.Service;
import pl.paweldyjak.checkout_service.entities.Item;
import pl.paweldyjak.checkout_service.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow();
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {

        return itemRepository.save(item);
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }
}
