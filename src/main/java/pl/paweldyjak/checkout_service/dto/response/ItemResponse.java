package pl.paweldyjak.checkout_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResponse {
    private Long id;
    private String name;

    @JsonProperty("normal_price")
    private BigDecimal normalPrice;

    @JsonProperty("required_quantity")
    private Integer requiredQuantity;

    @JsonProperty("special_price")
    private BigDecimal specialPrice;

    @JsonProperty("has_discount")
    public boolean hasDiscount() {
        return requiredQuantity != null && specialPrice != null;
    }
}

