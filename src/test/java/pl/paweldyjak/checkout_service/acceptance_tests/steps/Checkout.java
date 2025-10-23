package pl.paweldyjak.checkout_service.acceptance_tests.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import pl.paweldyjak.checkout_service.dtos.CheckoutItem;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponse;
import pl.paweldyjak.checkout_service.repositories.CheckoutRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static pl.paweldyjak.checkout_service.acceptance_tests.utils.Utils.getAuthorizationHeader;

public class Checkout {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl = "http://localhost:8080/api/checkouts";

    @Given("I create a new checkout")
    public void createNewCheckout() {
        ResponseEntity<CheckoutResponse> response = restTemplate.exchange(
                baseUrl + "/create",
                HttpMethod.POST,
                new HttpEntity<>(getAuthorizationHeader("customer", "customer")),
                CheckoutResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @And("I add item {string} with quantity {int} to checkout with ID {int}")
    public void addItems(String name, int quantity, int id) {
        List<CheckoutItem> itemsToAdd = List.of(new CheckoutItem(name, quantity));
        CheckoutResponse checkoutResponseBeforeAdding = getCheckoutById((long) id);
        List<CheckoutItem> itemsInCheckoutBeforeAdding = checkoutResponseBeforeAdding.items();
        List<CheckoutItem> expectedItemsInCheckoutAfterAdding = new ArrayList<>();

        for (CheckoutItem item : itemsInCheckoutBeforeAdding) {
            if (item.itemName().equals(name)) {
                quantity += item.quantity();
                expectedItemsInCheckoutAfterAdding.add(new CheckoutItem(name, quantity));
                break;
            }
        }

        ResponseEntity<CheckoutResponse> checkoutResponse = restTemplate.exchange(
                baseUrl + "/" + (long) id + "/add-items",
                HttpMethod.PATCH,
                new HttpEntity<>(itemsToAdd, getAuthorizationHeader("customer", "customer")),
                CheckoutResponse.class);

        assert checkoutResponse.getBody().items().equals(expectedItemsInCheckoutAfterAdding);
    }

    @And("I delete {int} items {string} from checkout with ID {int}")
    public void deleteItems(int quantity, String name, int id) {
        List<CheckoutItem> itemsToDelete = List.of(new CheckoutItem(name, quantity));
        List<CheckoutItem> expectedItemsInCheckoutAfterDeleting = new ArrayList<>();
        CheckoutResponse checkoutResponseBeforeDeleting = getCheckoutById(1L);

        for (CheckoutItem item : checkoutResponseBeforeDeleting.items()) {
            if (item.itemName().equals(name)) {
                quantity -= item.quantity();
                expectedItemsInCheckoutAfterDeleting.add(new CheckoutItem(name, quantity));
                break;
            }
        }
        ResponseEntity<CheckoutResponse> checkoutResponse = restTemplate.exchange(
                baseUrl + "/" + (long) id + "/delete-items",
                HttpMethod.PATCH,
                new HttpEntity<>(itemsToDelete, getAuthorizationHeader("customer", "customer")),
                CheckoutResponse.class);

        assert checkoutResponse.getBody().items().equals(expectedItemsInCheckoutAfterDeleting);
    }

    @And("Checkout with ID {int} is empty")
    public void checkoutIsEmpty(int id) {
        CheckoutResponse checkoutResponse = getCheckoutById((long) id);

        assert checkoutResponse.items().isEmpty();
    }

    @Then("I have following items in my checkout with ID {int}")
    public void iHaveFollowingItemsInMyCheckout(int id, DataTable dataTable) {
        List<CheckoutItem> expectedItemsList = new ArrayList<>();

        for (Map<String, String> item : dataTable.asMaps(String.class, String.class)) {
            String itemName = item.get("Item");
            int quantity = Integer.parseInt(item.get("Quantity"));
            CheckoutItem checkoutItem = new CheckoutItem(itemName, quantity);
            expectedItemsList.add(checkoutItem);
        }
        CheckoutResponse checkoutResponse = getCheckoutById((long) id);

        assert expectedItemsList.equals(checkoutResponse.items());
    }

    @Then("Adding item {string} with quantity {int} is not allowed")
    public void addingItemWithQuantityIsNotAllowed(String name, int quantity) {
        List<CheckoutItem> items = List.of(new CheckoutItem(name, quantity));

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/1/add-items",
                HttpMethod.PATCH,
                new HttpEntity<>(items, getAuthorizationHeader("customer", "customer")),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assert response.getBody().contains(String.format("Cannot add item - %s is unavailable", name));
    }

    private CheckoutResponse getCheckoutById(Long id) {
        ResponseEntity<CheckoutResponse> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.GET,
                new HttpEntity<>(getAuthorizationHeader("customer", "customer")),
                CheckoutResponse.class);

        return response.getBody();
    }
}
