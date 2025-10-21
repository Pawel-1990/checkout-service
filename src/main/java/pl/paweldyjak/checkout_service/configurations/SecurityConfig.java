package pl.paweldyjak.checkout_service.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {

        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()

                .requestMatchers(HttpMethod.GET, "/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/checkouts").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/checkouts/*/pay").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/api/checkouts/*/add-items").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/api/checkouts/*/delete-items").hasRole("CUSTOMER")

                .requestMatchers("/**").hasRole("ADMIN")

                .anyRequest().authenticated());

        // Allows using h2-console
        http.csrf(CsrfConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
