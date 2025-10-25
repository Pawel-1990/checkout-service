package pl.paweldyjak.checkout_service.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.paweldyjak.checkout_service.controllers.BundleDiscountController;
import pl.paweldyjak.checkout_service.unit_tests.utils.Utils;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountPatchRequestDto;
import pl.paweldyjak.checkout_service.dtos.request.BundleDiscountRequestDto;
import pl.paweldyjak.checkout_service.dtos.response.BundleDiscountResponseDto;
import pl.paweldyjak.checkout_service.services.BundleDiscountService;

import java.math.BigDecimal;
import java.util.Arrays;

@WebMvcTest(controllers = BundleDiscountController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        BundleDiscountResponseDto bundleDiscountResponseDto = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.ONE);

        when(checkoutService.getBundleDiscountResponseById(id)).thenReturn(bundleDiscountResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bundle-discounts/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.secondItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount").value(BigDecimal.ONE));

        verify(checkoutService).getBundleDiscountResponseById(id);
    }

    @Test
    public void testGetAllBundledDiscounts() throws Exception {
        Long id3 = 3L;
        Long id4 = 4L;
        BundleDiscountResponseDto bundleDiscountResponseDto = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.ONE);
        BundleDiscountResponseDto bundleDiscountResponseDto2 = Utils.buildBundleDiscountResponse(id2, id3, id4, BigDecimal.TEN);

        when(checkoutService.getAllBundledDiscounts()).thenReturn(Arrays.asList(bundleDiscountResponseDto, bundleDiscountResponseDto2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bundle-discounts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].secondItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].discountAmount").value(BigDecimal.ONE))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(id2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].secondItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].discountAmount").value(BigDecimal.TEN));

        verify(checkoutService).getAllBundledDiscounts();
    }

    @Test
    public void testCreateBundleDiscount() throws Exception {
        BundleDiscountRequestDto bundleDiscountRequestDto = Utils.buildBundleDiscountRequest(id, id2, BigDecimal.ONE);
        BundleDiscountResponseDto bundleDiscountResponseDto = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.ONE);

        String requestBody = objectMapper.writeValueAsString(bundleDiscountRequestDto);

        when(checkoutService.createBundleDiscount(bundleDiscountRequestDto)).thenReturn(bundleDiscountResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/bundle-discounts")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.secondItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount").value(BigDecimal.ONE));

        verify(checkoutService).createBundleDiscount(bundleDiscountRequestDto);
    }

    @Test
    public void testUpdateBundleDiscount() throws Exception {
        BundleDiscountRequestDto bundleDiscountRequestDto = Utils.buildBundleDiscountRequest(id, id2, BigDecimal.TEN);
        BundleDiscountResponseDto bundleDiscountResponseDto = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.TEN);

        String requestBody = objectMapper.writeValueAsString(bundleDiscountRequestDto);

        when(checkoutService.updateBundleDiscount(id, bundleDiscountRequestDto)).thenReturn(bundleDiscountResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/bundle-discounts/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.secondItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount").value(BigDecimal.TEN));

        verify(checkoutService).updateBundleDiscount(id, bundleDiscountRequestDto);
    }

    @Test
    public void BundleDiscountPatchRequest() throws Exception {
        BundleDiscountPatchRequestDto bundleDiscountPatchRequestDto = new BundleDiscountPatchRequestDto(null, null, BigDecimal.valueOf(15));
        BundleDiscountResponseDto bundleDiscountResponseDto = Utils.buildBundleDiscountResponse(id, id, id2, BigDecimal.valueOf(15));

        String requestBody = objectMapper.writeValueAsString(bundleDiscountPatchRequestDto);

        when(checkoutService.partialUpdateBundleDiscount(id, bundleDiscountPatchRequestDto)).thenReturn(bundleDiscountResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/bundle-discounts/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.secondItem").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount").value(BigDecimal.valueOf(15)));

        verify(checkoutService).partialUpdateBundleDiscount(id, bundleDiscountPatchRequestDto);
    }

    @Test
    public void testDeleteBundleDiscount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bundle-discounts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(checkoutService).deleteBundleDiscount(id);

    }
}
