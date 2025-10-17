package pl.paweldyjak.checkout_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.paweldyjak.checkout_service.entities.BundleDiscount;

public interface BundleDiscountRepository extends JpaRepository<BundleDiscount, Long> {

    @Query("SELECT COUNT(bd) > 0 FROM BundleDiscount bd WHERE " +
            "(bd.firstItem.id = :itemId OR bd.secondItem.id = :itemId) AND bd.active = true")
    boolean checkIfActiveDiscountExistsForItem(@Param("itemId") Long itemId);

    @Modifying
    @Query("DELETE FROM BundleDiscount bd WHERE " +
            "(bd.firstItem.id = :itemId OR bd.secondItem.id = :itemId) AND bd.active = false")
    void deleteInactiveDiscountsForItem(@Param("itemId") Long itemId);

    @Modifying
    @Query("UPDATE BundleDiscount bd SET bd.active = false WHERE (bd.id = :bundleDiscountId) AND bd.active = true")
    void deactivateDiscountsById(@Param("bundleDiscountId") Long itemId);

    @Modifying
    @Query("UPDATE BundleDiscount bd SET bd.active = false WHERE (bd.firstItem.id = :itemId OR bd.secondItem.id = :itemId) AND bd.active = true")
    void deactivateDiscountsByItemId(@Param("itemId") Long itemId);
}
