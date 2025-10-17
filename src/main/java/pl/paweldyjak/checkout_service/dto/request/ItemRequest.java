package pl.paweldyjak.checkout_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Normal price cannot be null")
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