package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dto.request.ItemRequest;
import pl.paweldyjak.checkout_service.dto.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.Item;

@Component
public class ItemMapper {

    public ItemResponse mapToItemResponse(Item item) {
        if (item == null) {
            return null;
        }

        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getNormalPrice(),
                item.getRequiredQuantity(),
                item.getSpecialPrice()
        );
    }

    public Item mapToItemEntity(ItemRequest request) {
        if (request == null) {
            return null;
        }

        Item item = new Item();
        item.setName(request.getName());
        item.setNormalPrice(request.getNormalPrice());
        item.setRequiredQuantity(request.getRequiredQuantity());
        item.setSpecialPrice(request.getSpecialPrice());

        return item;
    }

    public void updateItemEntity(Item item, ItemRequest itemRequest) {
        if (item == null || itemRequest == null) {
            return;
        }
        item.setName(itemRequest.getName());
        item.setNormalPrice(itemRequest.getNormalPrice());
        item.setRequiredQuantity(itemRequest.getRequiredQuantity());
        item.setSpecialPrice(itemRequest.getSpecialPrice());
    }
}

