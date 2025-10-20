package pl.paweldyjak.acceptance_tests.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import pl.paweldyjak.checkout_service.dtos.request.ItemsToModifyRequest;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.repositories.CheckoutRepository;

import java.util.List;

public class Checkout {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl = "http://localhost:8080/api/checkouts";

    @Given("I create a new checkout")
    public void createNewCheckout() {
        CheckoutResponse checkoutResponse = restTemplate.postForObject(baseUrl, null, CheckoutResponse.class);
    }

    @And("I add item {string} with quantity {int}")
    public void addItemsWithQuantity(String name, int quantity) {
        List<ItemsToModifyRequest> items = List.of(new ItemsToModifyRequest(name, quantity));
        CheckoutResponse checkoutResponse = restTemplate.patchForObject(baseUrl + "/1/add-items", items, CheckoutResponse.class);
        assert checkoutResponse.items().stream().anyMatch(i -> i.itemName().equals(name) && i.quantity() == quantity);
    }
}
