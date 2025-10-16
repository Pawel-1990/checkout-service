package pl.paweldyjak.checkout_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bundle_discounts")
public class BundleDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_item_id", nullable = false)
    private Item firstItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_item_id", nullable = false)
    private Item secondItemId;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    public BundleDiscount(Item firstItemId, Item secondItemId, BigDecimal discountAmount) {
        this.firstItemId = firstItemId;
        this.secondItemId = secondItemId;
        this.discountAmount = discountAmount;
    }
}
