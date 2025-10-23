package pl.paweldyjak.checkout_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;

import java.math.BigDecimal;
import java.util.List;

public interface BundleDiscountRepository extends JpaRepository<BundleDiscount, Long> {

    @Modifying
    @Query("DELETE FROM BundleDiscount bd WHERE " +
            "(bd.firstItem.id = :itemId OR bd.secondItem.id = :itemId)")
    void deleteDiscountsForItem(@Param("itemId") Long itemId);

    @Query("""
        SELECT COALESCE(SUM(bd.discountAmount), 0)
        FROM BundleDiscount bd
        WHERE bd.firstItem.name IN :itemNames
          AND bd.secondItem.name IN :itemNames
    """)
    BigDecimal getSumDiscountsForItemNames(@Param("itemNames") List<String> itemNames);
}
