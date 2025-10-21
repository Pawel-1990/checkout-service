package pl.paweldyjak.unit_tests.checkout_service.controllers;

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
import pl.paweldyjak.checkout_service.controllers.CheckoutController;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemInfo;
import pl.paweldyjak.unit_tests.checkout_service.utils.Utils;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;
import pl.paweldyjak.checkout_service.services.CheckoutService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CheckoutControllerTest {
    private final Long id = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CheckoutService checkoutService;

    @Test
    public void testGetCheckoutById() throws Exception {
        CheckoutResponse checkoutResponse = Utils.buildCheckoutResponse(id);

        when(checkoutService.getCheckoutById(id)).thenReturn(checkoutResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/checkouts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price_before_discount").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_discount").value(BigDecimal.valueOf(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.final_price").value(BigDecimal.valueOf(40)));

        verify(checkoutService).getCheckoutById(id);
    }

    @Test
    public void testGetAllCheckouts() throws Exception {
        Long id2 = 2L;
        CheckoutResponse checkoutResponse = Utils.buildCheckoutResponse(id);
        CheckoutResponse checkoutResponse2 = Utils.buildCheckoutResponse(id2);

        when(checkoutService.getAllCheckouts()).thenReturn(Arrays.asList(checkoutResponse, checkoutResponse2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/checkouts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price_before_discount").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].total_discount").value(BigDecimal.valueOf(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].final_price").value(BigDecimal.valueOf(40)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(id2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price_before_discount").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].total_discount").value(BigDecimal.valueOf(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].final_price").value(BigDecimal.valueOf(40)));

        verify(checkoutService).getAllCheckouts();
    }

    @Test
    public void testCreateCheckout() throws Exception {
        CheckoutResponse checkoutResponse = Utils.buildCheckoutResponse(id);

        when(checkoutService.createCheckout()).thenReturn(checkoutResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checkouts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price_before_discount").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_discount").value(BigDecimal.valueOf(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.final_price").value(BigDecimal.valueOf(40)));

        verify(checkoutService).createCheckout();
    }

    @Test
    public void testPay() throws Exception {
        ReceiptResponse receiptResponse = ReceiptResponse.builder()
                .checkoutId(id)
                .status(CheckoutStatus.ACTIVE)
                .priceBeforeDiscount(BigDecimal.valueOf(50))
                .totalDiscount(BigDecimal.valueOf(10))
                .finalPrice(BigDecimal.valueOf(40))
                .build();

        when(checkoutService.pay(id)).thenReturn(receiptResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/checkouts/{id}/pay", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.checkout_id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price_before_discount").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_discount").value(BigDecimal.valueOf(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.final_price").value(BigDecimal.valueOf(40)));

        verify(checkoutService).pay(id);
    }

    @Test
    public void testAddItemsToCheckout() throws Exception {
        CheckoutResponse checkoutResponse = Utils.buildCheckoutResponse(id);
        CheckoutItemInfo itemsToModifyRequest = CheckoutItemInfo.builder()
                .itemName("Apple")
                .quantity(5)
                .build();

        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(itemsToModifyRequest));

        when(checkoutService.addItemsToCheckout(id, Collections.singletonList(itemsToModifyRequest))).thenReturn(checkoutResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/checkouts/{id}/add-items", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price_before_discount").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_discount").value(BigDecimal.valueOf(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.final_price").value(BigDecimal.valueOf(40)));

        verify(checkoutService).addItemsToCheckout(id, Collections.singletonList(itemsToModifyRequest));
    }

    @Test
    public void testDeleteItemsFromCheckout() throws Exception {
        CheckoutResponse checkoutResponse = Utils.buildCheckoutResponse(id);
        CheckoutItemInfo itemsToModifyRequest = CheckoutItemInfo.builder()
                .itemName("Apple")
                .quantity(5)
                .build();

        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(itemsToModifyRequest));

        when(checkoutService.deleteItemsFromCheckout(id, Collections.singletonList(itemsToModifyRequest))).thenReturn(checkoutResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/checkouts/{id}/delete-items", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price_before_discount").value(BigDecimal.valueOf(50)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_discount").value(BigDecimal.valueOf(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.final_price").value(BigDecimal.valueOf(40)));

        verify(checkoutService).deleteItemsFromCheckout(id, Collections.singletonList(itemsToModifyRequest));
    }

    @Test
    public void testDeleteCheckout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/checkouts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(checkoutService).deleteCheckout(id);
    }
}
