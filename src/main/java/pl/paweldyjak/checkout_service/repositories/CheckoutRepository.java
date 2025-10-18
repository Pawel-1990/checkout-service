package pl.paweldyjak.checkout_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.paweldyjak.checkout_service.entities.Checkout;

import java.util.Map;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    @Query("SELECT c.items FROM Checkout c WHERE c.id = :id")
    Map<String, Integer> findItemsByCheckoutId(@Param("id") Long id);
}
