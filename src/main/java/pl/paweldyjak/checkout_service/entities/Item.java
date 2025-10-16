package pl.paweldyjak.checkout_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "normal_price", nullable = false)
    BigDecimal normalPrice;

    @Column(name = "required_quantity")
    int requiredQuantity;

    @Column(name = "special_price")
    BigDecimal specialPrice;

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
