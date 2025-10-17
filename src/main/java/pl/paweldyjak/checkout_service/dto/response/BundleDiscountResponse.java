package pl.paweldyjak.checkout_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BundleDiscountResponse {
    private Long id;

    @JsonProperty("first_item")
    private ItemResponse firstItem;

    @JsonProperty("second_item")
    private ItemResponse secondItem;

    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;
}

