/*
package pl.paweldyjak.acceptance_tests.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemInfo;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.repositories.CheckoutRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    public void addItems(String name, int quantity) {
        List<CheckoutItemInfo> itemsToAdd = List.of(new CheckoutItemInfo(name, quantity));
        CheckoutResponse checkoutResponseBeforeAdding = getCheckoutById(1L);
        List<CheckoutItemInfo> itemsInCheckoutBeforeAdding = checkoutResponseBeforeAdding.items();
        List<CheckoutItemInfo> expectedItemsInCheckoutAfterAdding = new ArrayList<>();

        for (CheckoutItemInfo item : itemsInCheckoutBeforeAdding) {
            if (item.itemName().equals(name)) {
                quantity += item.quantity();
                expectedItemsInCheckoutAfterAdding.add(new CheckoutItemInfo(name, quantity));
                break;
            }
        }
        CheckoutResponse checkoutResponse = restTemplate.patchForObject(baseUrl + "/1/add-items", itemsToAdd, CheckoutResponse.class);

        assert checkoutResponse.items().equals(expectedItemsInCheckoutAfterAdding);
    }

    @And("I delete {int} items {string} from checkout")
    public void deleteItems(int quantity, String name) {
        List<CheckoutItemInfo> itemsToDelete = List.of(new CheckoutItemInfo(name, quantity));
        CheckoutResponse checkoutResponseBeforeDeleting = getCheckoutById(1L);
        List<CheckoutItemInfo> itemsInCheckoutBeforeDeleting = checkoutResponseBeforeDeleting.items();
        List<CheckoutItemInfo> expectedItemsInCheckoutAfterDeleting = new ArrayList<>();

        for (CheckoutItemInfo item : itemsInCheckoutBeforeDeleting) {
            if (item.itemName().equals(name)) {
                quantity -= item.quantity();
                expectedItemsInCheckoutAfterDeleting.add(new CheckoutItemInfo(name, quantity));
                break;
            }
        }
        CheckoutResponse checkoutResponse = restTemplate.patchForObject(baseUrl + "/1/add-items", itemsToDelete, CheckoutResponse.class);

        assert checkoutResponse.items().equals(expectedItemsInCheckoutAfterDeleting);
    }

    @And("Checkout is empty")
    public void checkoutIsEmpty() {
        CheckoutResponse checkoutResponse = getCheckoutById(1L);
        assert checkoutResponse.items().isEmpty();
    }

    @Then("I have following items in my checkout")
    public void iHaveFollowingItemsInMyCheckout(DataTable dataTable) {
        List<CheckoutItemInfo> expectedItemsList = new ArrayList<>();
        List<Map<String, String>> items = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> item : items) {
            String itemName = item.get("Item");
            int quantity = Integer.parseInt(item.get("Quantity"));
            CheckoutItemInfo checkoutItemInfo = new CheckoutItemInfo(itemName, quantity);
            expectedItemsList.add(checkoutItemInfo);
        }

        CheckoutResponse checkoutResponse = restTemplate.getForObject(baseUrl + "/1", CheckoutResponse.class);

        assert expectedItemsList.equals(checkoutResponse.items());
    }

    @Then("Adding item {string} with quantity {int} is not allowed")
    public void addingItemWithQuantityIsNotAllowed(String name, int quantity) {
        List<CheckoutItemInfo> items = List.of(new CheckoutItemInfo(name, quantity));

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/1/add-items",
                HttpMethod.PATCH,
                new HttpEntity<>(items),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assert response.getBody().contains(String.format("Cannot add item - %s is unavailable", name));
    }

    private CheckoutResponse getCheckoutById(Long id) {
        return restTemplate.getForObject(baseUrl + "/" + id, CheckoutResponse.class);
    }
}
*/
