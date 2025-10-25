package pl.paweldyjak.checkout_service.mappers;

import org.springframework.stereotype.Component;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequestDto;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponseDto;
import pl.paweldyjak.checkout_service.entities.Item;

@Component
public class ItemMapper {

    public ItemResponseDto mapToItemResponse(Item item) {
        if (item == null) {
            return null;
        }

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .normalPrice(item.getNormalPrice())
                .requiredQuantity(item.getRequiredQuantity())
                .specialPrice(item.getSpecialPrice())
                .build();
    }

    public Item mapToItemEntity(ItemRequestDto request) {
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

    public Item updateItemEntity(Item item, ItemRequestDto itemRequestDto) {
        if (item == null || itemRequestDto == null) {
            return null;
        }
        item.setName(itemRequestDto.name());
        item.setNormalPrice(itemRequestDto.normalPrice());
        item.setRequiredQuantity(itemRequestDto.requiredQuantity());
        item.setSpecialPrice(itemRequestDto.specialPrice());
        return item;
    }
}

