/*
package pl.paweldyjak.acceptance_tests.steps;

import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import pl.paweldyjak.checkout_service.repositories.ItemRepository;

import java.util.List;

public class Item {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "http://localhost:8080/api/items";

    @And("Item {string} is available to buy")
    public void itemIsAvailable(String itemName) {
        List<String> items = restTemplate.exchange(
                baseUrl + "/names",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        ).getBody();
        assert items.stream().anyMatch(n -> n.equals(itemName));
    }

    @And("Item {string} is not available to buy")
    public void itemIsNotAvailable(String itemName) {
        List<String> items = restTemplate.exchange(
                baseUrl + "/names",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        ).getBody();
        assert items.stream().noneMatch(n -> n.equals(itemName));
    }

}
*/
