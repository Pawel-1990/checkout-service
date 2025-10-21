package pl.paweldyjak.checkout_service.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.paweldyjak.checkout_service.controllers.ItemController;
import pl.paweldyjak.checkout_service.unit_tests.utils.Utils;
import pl.paweldyjak.checkout_service.dtos.request.ItemPatchRequest;
import pl.paweldyjak.checkout_service.dtos.request.ItemRequest;
import pl.paweldyjak.checkout_service.dtos.response.ItemResponse;
import pl.paweldyjak.checkout_service.services.ItemService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ItemControllerTest {
    private final Long id = 1L;
    private final Long id2 = 2L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    ItemService itemService;

    @Test
    public void testGetItemById() throws Exception {
        ItemResponse itemResponse = Utils.buildItemResponse(id);

        when(itemService.getItemById(id)).thenReturn(itemResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Apple"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.normalPrice").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requiredQuantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.specialPrice").value(BigDecimal.valueOf(40)));

        verify(itemService).getItemById(id);
    }

    @Test
    public void testGetAllItems() throws Exception {
        ItemResponse itemResponse = Utils.buildItemResponse(id);
        ItemResponse itemResponse2 = Utils.buildItemResponse(id2);

        when(itemService.getAllItems()).thenReturn(Arrays.asList(itemResponse, itemResponse2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Apple"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].normalPrice").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requiredQuantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialPrice").value(BigDecimal.valueOf(40)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(id2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Apple"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].normalPrice").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].requiredQuantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].specialPrice").value(BigDecimal.valueOf(40)));

        verify(itemService).getAllItems();
    }

    @Test
    public void testGetAllAvailableItemNames() throws Exception {
        List<String> itemNames = Arrays.asList("Apple", "Banana", "Orange");

        when(itemService.getAllAvailableItemNames()).thenReturn(itemNames);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/names")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("Apple"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value("Banana"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2]").value("Orange"));

        verify(itemService).getAllAvailableItemNames();
    }

    @Test
    public void testCreateItem() throws Exception {
        ItemResponse itemResponse = Utils.buildItemResponse(id);
        ItemRequest itemRequest = Utils.buildItemRequest();

        String requestBody = objectMapper.writeValueAsString(itemRequest);

        when(itemService.updateItem(id, itemRequest)).thenReturn(itemResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/items/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Apple"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.normalPrice").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requiredQuantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.specialPrice").value(BigDecimal.valueOf(40)));

        verify(itemService).updateItem(id, itemRequest);
    }

    @Test
    public void testUpdateItem() throws Exception {
        ItemResponse itemResponse = Utils.buildItemResponse(id);
        ItemRequest itemRequest = Utils.buildItemRequest();

        String requestBody = objectMapper.writeValueAsString(itemRequest);

        when(itemService.updateItem(id, itemRequest)).thenReturn(itemResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/items/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Apple"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.normalPrice").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requiredQuantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.specialPrice").value(BigDecimal.valueOf(40)));

        verify(itemService).updateItem(id, itemRequest);
    }

    @Test
    public void testPartialUpdateItem() throws Exception {
        ItemResponse itemResponse = Utils.buildItemResponse(id);
        ItemPatchRequest itemPatchRequest = ItemPatchRequest.builder()
                .name("Apple")
                .normalPrice(BigDecimal.valueOf(50))
                .requiredQuantity(3)
                .specialPrice(BigDecimal.valueOf(40))
                .build();

        String requestBody = objectMapper.writeValueAsString(itemPatchRequest);

        when(itemService.partialUpdateItem(id, itemPatchRequest)).thenReturn(itemResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/items/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Apple"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.normalPrice").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requiredQuantity").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.specialPrice").value(BigDecimal.valueOf(40)));

        verify(itemService).partialUpdateItem(id, itemPatchRequest);
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/items/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(itemService).deleteItem(id);
    }
}
