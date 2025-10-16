package pl.paweldyjak.checkout_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "First item cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_item_id", nullable = false)
    private Item firstItem;

    @NotNull(message = "Second item cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_item_id", nullable = false)
    private Item secondItem;

    @NotNull(message = "Discount amount cannot be null")
    @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public BundleDiscount(Item firstItem, Item secondItem, BigDecimal discountAmount) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.discountAmount = discountAmount;
    }
}
