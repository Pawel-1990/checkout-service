package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequest;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponse;
import pl.paweldyjak.checkout_service.entities.Item;

@Component
public class ItemMapper {

    public ItemResponse mapToItemResponse(Item item) {
        if (item == null) {
            return null;
        }

        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .normalPrice(item.getNormalPrice())
                .requiredQuantity(item.getRequiredQuantity())
                .specialPrice(item.getSpecialPrice())
                .build();
    }

    public Item mapToItemEntity(ItemRequest request) {
        if (request == null) {
            return null;
        }

        Item item = new Item();
        item.setName(request.name());
        item.setNormalPrice(request.normalPrice());
        item.setRequiredQuantity(request.requiredQuantity());
        item.setSpecialPrice(request.specialPrice());

        return item;
    }

    public void updateItemEntity(Item item, ItemRequest itemRequest) {
        if (item == null || itemRequest == null) {
            return;
        }
        item.setName(itemRequest.name());
        item.setNormalPrice(itemRequest.normalPrice());
        item.setRequiredQuantity(itemRequest.requiredQuantity());
        item.setSpecialPrice(itemRequest.specialPrice());
    }
}

