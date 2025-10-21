package pl.paweldyjak.acceptance_tests;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.paweldyjak.checkout_service.CheckoutServiceApplication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CheckoutServiceApplication.class)
public class CucumberTest {
}
