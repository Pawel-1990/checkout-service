package pl.paweldyjak.checkout_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotNull(message = "Normal price cannot be null")
    @DecimalMin(value = "0.01", message = "Normal price must be greater than 0")
    @Column(name = "normal_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal normalPrice;

    @Min(value = 2, message = "Required quantity must be at least 2")
    @Column(name = "required_quantity")
    private Integer requiredQuantity;

    @DecimalMin(value = "0.01", message = "Special price must be greater than 0")
    @Column(name = "special_price", precision = 19, scale = 2)
    private BigDecimal specialPrice;

    public Item(String name, BigDecimal normalPrice) {
        this.name = name;
        this.normalPrice = normalPrice;
    }

    public Item(String name, BigDecimal normalPrice, int requiredQuantity, BigDecimal specialPrice) {
        this.name = name;
        this.normalPrice = normalPrice;
        this.requiredQuantity = requiredQuantity;
        this.specialPrice = specialPrice;
    }
}
