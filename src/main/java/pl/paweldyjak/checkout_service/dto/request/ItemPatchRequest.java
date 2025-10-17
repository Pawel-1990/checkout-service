package pl.paweldyjak.checkout_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemPatchRequest {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @DecimalMin(value = "0.01", message = "Normal price must be greater than 0")
    @JsonProperty("normal_price")
    private BigDecimal normalPrice;

    @Min(value = 2, message = "Required quantity must be at least 2")
    @JsonProperty("required_quantity")
    private Integer requiredQuantity;

    @DecimalMin(value = "0.01", message = "Special price must be greater than 0")
    @JsonProperty("special_price")
    private BigDecimal specialPrice;
}