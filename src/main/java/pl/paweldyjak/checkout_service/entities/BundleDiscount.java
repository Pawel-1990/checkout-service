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
@Table(
        name = "bundle_discounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"first_item_id", "second_item_id"})
)
public class BundleDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_item_id", nullable = false)
    private Item firstItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_item_id", nullable = false)
    private Item secondItem;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public BundleDiscount(Item firstItem, Item secondItem, BigDecimal discountAmount) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.discountAmount = discountAmount;
    }
}
