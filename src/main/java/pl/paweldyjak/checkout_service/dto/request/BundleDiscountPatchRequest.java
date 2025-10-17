package pl.paweldyjak.checkout_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BundleDiscountPatchRequest {

    @JsonProperty("first_item_id")
    private Long firstItemId;

    @JsonProperty("second_item_id")
    private Long secondItemId;

    @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;
}
