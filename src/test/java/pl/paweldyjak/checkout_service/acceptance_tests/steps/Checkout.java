package pl.paweldyjak.checkout_service.acceptance_tests.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import pl.paweldyjak.checkout_service.dtos.CheckoutItemDto;
import pl.paweldyjak.checkout_service.dtos.response.CheckoutResponseDto;
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
        ResponseEntity<CheckoutResponseDto> response = restTemplate.exchange(
                baseUrl + "/create",
                HttpMethod.POST,
                new HttpEntity<>(getAuthorizationHeader("customer", "customer")),
                CheckoutResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @And("I add item {string} with quantity {int} to checkout with ID {int}")
    public void addItems(String name, int quantity, int id) {
        List<CheckoutItemDto> itemsToAdd = List.of(new CheckoutItemDto(name, quantity));
        CheckoutResponseDto checkoutResponseDtoBeforeAdding = getCheckoutById((long) id);
        List<CheckoutItemDto> itemsInCheckoutBeforeAdding = checkoutResponseDtoBeforeAdding.items();
        List<CheckoutItemDto> expectedItemsInCheckoutAfterAdding = new ArrayList<>();

        for (CheckoutItemDto item : itemsInCheckoutBeforeAdding) {
            if (item.itemName().equals(name)) {
                quantity += item.quantity();
                expectedItemsInCheckoutAfterAdding.add(new CheckoutItemDto(name, quantity));
                break;
            }
        }

        ResponseEntity<CheckoutResponseDto> checkoutResponse = restTemplate.exchange(
                baseUrl + "/" + (long) id + "/add-items",
                HttpMethod.PATCH,
                new HttpEntity<>(itemsToAdd, getAuthorizationHeader("customer", "customer")),
                CheckoutResponseDto.class);

        assert checkoutResponse.getBody().items().equals(expectedItemsInCheckoutAfterAdding);
    }

    @And("I delete {int} items {string} from checkout with ID {int}")
    public void deleteItems(int quantity, String name, int id) {
        List<CheckoutItemDto> itemsToDelete = List.of(new CheckoutItemDto(name, quantity));
        List<CheckoutItemDto> expectedItemsInCheckoutAfterDeleting = new ArrayList<>();
        CheckoutResponseDto checkoutResponseDtoBeforeDeleting = getCheckoutById(1L);

        for (CheckoutItemDto item : checkoutResponseDtoBeforeDeleting.items()) {
            if (item.itemName().equals(name)) {
                quantity -= item.quantity();
                expectedItemsInCheckoutAfterDeleting.add(new CheckoutItemDto(name, quantity));
                break;
            }
        }
        ResponseEntity<CheckoutResponseDto> checkoutResponse = restTemplate.exchange(
                baseUrl + "/" + (long) id + "/delete-items",
                HttpMethod.PATCH,
                new HttpEntity<>(itemsToDelete, getAuthorizationHeader("customer", "customer")),
                CheckoutResponseDto.class);

        assert checkoutResponse.getBody().items().equals(expectedItemsInCheckoutAfterDeleting);
    }

    @And("Checkout with ID {int} is empty")
    public void checkoutIsEmpty(int id) {
        CheckoutResponseDto checkoutResponseDto = getCheckoutById((long) id);

        assert checkoutResponseDto.items().isEmpty();
    }

    @Then("I have following items in my checkout with ID {int}")
    public void iHaveFollowingItemsInMyCheckout(int id, DataTable dataTable) {
        List<CheckoutItemDto> expectedItemsList = new ArrayList<>();

        for (Map<String, String> item : dataTable.asMaps(String.class, String.class)) {
            String itemName = item.get("Item");
            int quantity = Integer.parseInt(item.get("Quantity"));
            CheckoutItemDto checkoutItemDto = new CheckoutItemDto(itemName, quantity);
            expectedItemsList.add(checkoutItemDto);
        }
        CheckoutResponseDto checkoutResponseDto = getCheckoutById((long) id);

        assert expectedItemsList.equals(checkoutResponseDto.items());
    }

    @Then("Adding item {string} with quantity {int} is not allowed")
    public void addingItemWithQuantityIsNotAllowed(String name, int quantity) {
        List<CheckoutItemDto> items = List.of(new CheckoutItemDto(name, quantity));

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/1/add-items",
                HttpMethod.PATCH,
                new HttpEntity<>(items, getAuthorizationHeader("customer", "customer")),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assert response.getBody().contains(String.format("Cannot add item - %s is unavailable", name));
    }

    private CheckoutResponseDto getCheckoutById(Long id) {
        ResponseEntity<CheckoutResponseDto> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.GET,
                new HttpEntity<>(getAuthorizationHeader("customer", "customer")),
                CheckoutResponseDto.class);

        return response.getBody();
    }
}
