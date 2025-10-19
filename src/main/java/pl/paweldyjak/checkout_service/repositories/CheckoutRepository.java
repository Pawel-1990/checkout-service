package pl.paweldyjak.checkout_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.paweldyjak.checkout_service.entities.Checkout;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
}
