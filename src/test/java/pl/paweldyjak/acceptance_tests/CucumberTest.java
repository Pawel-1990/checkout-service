package pl.paweldyjak.acceptance_tests;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.paweldyjak.checkout_service.CheckoutServiceApplication;

@CucumberContextConfiguration
@SpringBootTest
@ContextConfiguration(classes = CheckoutServiceApplication.class)
public class CucumberTest {
}
