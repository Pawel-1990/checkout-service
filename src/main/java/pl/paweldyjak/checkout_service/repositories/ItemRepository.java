package pl.paweldyjak.checkout_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.paweldyjak.checkout_service.entities.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i.name FROM Item i")
    List<String> findAllAvailableItemNames();
}
