package pl.paweldyjak.unit_tests.checkout_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.paweldyjak.checkout_service.CheckoutServiceApplication;
import pl.paweldyjak.checkout_service.controllers.BundleDiscountController;
import pl.paweldyjak.unit_tests.checkout_service.utils.Utils;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequest;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequest;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponse;
import pl.paweldyjak.checkout_service.services.BundleDiscountService;

import java.math.BigDecimal;
import java.util.Arrays;

@WebMvcTest(controllers = CheckoutServiceApplication.class)
public class BundleDiscountControllerTest {
    private final Long id = 1L;
    private final Long id2 = 2L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BundleDiscountService checkoutService;

    @Test
    public void testGetBundleDiscountById() throws Exception {
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.ONE);

        when(checkoutService.getBundleDiscountResponseById(id)).thenReturn(bundleDiscountResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bundle-discounts/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.second_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discount_amount").value(BigDecimal.ONE));

        verify(checkoutService).getBundleDiscountResponseById(id);
    }

    @Test
    public void testGetAllBundledDiscounts() throws Exception {
        Long id3 = 3L;
        Long id4 = 4L;
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.ONE);
        BundleDiscountResponse bundleDiscountResponse2 = Utils.buildBundleDiscountResponse(id2, id3, id4, BigDecimal.TEN);

        when(checkoutService.getAllBundledDiscounts()).thenReturn(Arrays.asList(bundleDiscountResponse, bundleDiscountResponse2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bundle-discounts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].first_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].second_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].discount_amount").value(BigDecimal.ONE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(id2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].first_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].second_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].discount_amount").value(BigDecimal.TEN));

        verify(checkoutService).getAllBundledDiscounts();
    }

    @Test
    public void testCreateBundleDiscount() throws Exception {
        BundleDiscountRequest bundleDiscountRequest = Utils.buildBundleDiscountRequest(id, id2, BigDecimal.ONE);
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.ONE);

        String requestBody = objectMapper.writeValueAsString(bundleDiscountRequest);

        when(checkoutService.createBundleDiscount(bundleDiscountRequest)).thenReturn(bundleDiscountResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/bundle-discounts")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.second_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discount_amount").value(BigDecimal.ONE));

        verify(checkoutService).createBundleDiscount(bundleDiscountRequest);
    }

    @Test
    public void testUpdateBundleDiscount() throws Exception {
        BundleDiscountRequest bundleDiscountRequest = Utils.buildBundleDiscountRequest(id, id2, BigDecimal.TEN);
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.TEN);

        String requestBody = objectMapper.writeValueAsString(bundleDiscountRequest);

        when(checkoutService.updateBundleDiscount(id, bundleDiscountRequest)).thenReturn(bundleDiscountResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/bundle-discounts/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.second_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discount_amount").value(BigDecimal.TEN));

        verify(checkoutService).updateBundleDiscount(id, bundleDiscountRequest);
    }

    @Test
    public void BundleDiscountPatchRequest() throws Exception {
        BundleDiscountPatchRequest bundleDiscountPatchRequest = new BundleDiscountPatchRequest(null, null, BigDecimal.valueOf(15));
        BundleDiscountResponse bundleDiscountResponse = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.valueOf(15));

        String requestBody = objectMapper.writeValueAsString(bundleDiscountPatchRequest);

        when(checkoutService.partialUpdateBundleDiscount(id, bundleDiscountPatchRequest)).thenReturn(bundleDiscountResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/bundle-discounts/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.second_item").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discount_amount").value(BigDecimal.valueOf(15)));

        verify(checkoutService).partialUpdateBundleDiscount(id, bundleDiscountPatchRequest);
    }

    @Test
    public void testDeleteBundleDiscount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundle-discounts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(checkoutService).deleteBundleDiscount(id);

    }
}
