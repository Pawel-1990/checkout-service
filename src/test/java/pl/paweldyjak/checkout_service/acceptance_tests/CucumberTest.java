package pl.paweldyjak.checkout_service.acceptance_tests;


import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.paweldyjak.checkout_service.CheckoutServiceApplication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CheckoutServiceApplication.class)
@CucumberContextConfiguration
public class CucumberTest {
}

