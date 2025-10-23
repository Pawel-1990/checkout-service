package pl.paweldyjak.checkout_service.acceptance_tests.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Utils {

    public static HttpHeaders getAuthorizationHeader(String username, String password) {
        String base64Creds = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
