package pl.paweldyjak.checkout_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.paweldyjak.checkout_service.mappers.ReceiptDataConverter;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponse;
import pl.paweldyjak.checkout_service.enums.CheckoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "checkout")
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CheckoutStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "checkout_items",
            joinColumns = @JoinColumn(name = "checkout_id")
    )
    @MapKeyColumn(name = "item_name", length = 100)
    @Column(name = "quantity")
    private Map<String, Integer> items = new HashMap<>();


    @Column(name = "receipt", columnDefinition = "TEXT")
    @Convert(converter = ReceiptDataConverter.class)
    private ReceiptResponse receipt;

    @Column(name = "price_before_discount", precision = 19, scale = 2)
    private BigDecimal priceBeforeDiscount;

    @Column(name = "total_discount", precision = 19, scale = 2)
    private BigDecimal totalDiscount;

    @Column(name = "final_price", precision = 19, scale = 2)
    private BigDecimal finalPrice;

    public static Checkout create() {
        Checkout checkout = new Checkout();
        checkout.setCreatedAt(LocalDateTime.now());
        checkout.setStatus(CheckoutStatus.ACTIVE);
        checkout.setPriceBeforeDiscount(BigDecimal.ZERO);
        checkout.setTotalDiscount(BigDecimal.ZERO);
        checkout.setFinalPrice(BigDecimal.ZERO);

        return checkout;
    }
}
