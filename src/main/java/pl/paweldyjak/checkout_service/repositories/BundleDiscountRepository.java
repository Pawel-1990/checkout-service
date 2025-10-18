package pl.paweldyjak.checkout_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;

public interface BundleDiscountRepository extends JpaRepository<BundleDiscount, Long> {

    @Modifying
    @Query("DELETE FROM BundleDiscount bd WHERE " +
            "(bd.firstItem.id = :itemId OR bd.secondItem.id = :itemId)")
    void deleteDiscountsForItem(@Param("itemId") Long itemId);
}
