package pl.paweldyjak.checkout_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.paweldyjak.checkout_service.entities.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
