package pl.paweldyjak.checkout_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
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

    @OneToMany(mappedBy = "firstItemId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BundleDiscount> discountsAsFirstItem = new HashSet<>();

    @OneToMany(mappedBy = "secondItemId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BundleDiscount> discountsAsSecondItem = new HashSet<>();

    public Item(String name, BigDecimal normalPrice, int requiredQuantity, BigDecimal specialPrice) {
        this.name = name;
        this.normalPrice = normalPrice;
        this.requiredQuantity = requiredQuantity;
        this.specialPrice = specialPrice;
    }
}
