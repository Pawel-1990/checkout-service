package pl.paweldyjak.checkout_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BundleDiscountRequest {

    @NotNull(message = "First item ID cannot be null")
    @JsonProperty("first_item_id")
    private Long firstItemId;

    @NotNull(message = "Second item ID cannot be null")
    @JsonProperty("second_item_id")
    private Long secondItemId;

    @NotNull(message = "Discount amount cannot be null")
    @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;
}
