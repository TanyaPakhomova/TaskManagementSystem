package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.Credentials;
import com.tpakhomova.tms.api.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demo for main functionality: task management and authentication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class DemoTest {
    static final User TANYA_USER = new User("tanya", "123456789", "email@mail.com", "Tanya", "Pakhomova");
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;

    @Test
    void validUserOperations() {
        // Register
        assertThat(rest.postForObject(url("/register"), TANYA_USER, String.class))
                .contains("Registered successfully");

        // Login
        String token = rest.postForObject(
                url("/login"),
                new Credentials(TANYA_USER.email(), TANYA_USER.passHash()),
                String.class
        );
        assertThat(token).isNotNull();

        ResponseEntity<String> result = rest.exchange(
                url("/unregister/" + TANYA_USER.email()),
                HttpMethod.DELETE,
                authorizedHeader(token),
                String.class
        );
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @NotNull
    private static HttpEntity<Object> authorizedHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    @Test
    void invalidUserOperations() {
        // Register twice
        assertThat(rest.postForObject(url("/register"), TANYA_USER, String.class))
                .contains("Registered successfully");
        boolean isClientError = rest.postForEntity(
                url("/register"),
                TANYA_USER,
                String.class
        ).getStatusCode().is4xxClientError();
        assertThat(isClientError).isTrue();

        // Can not login with wrong email
        isClientError = rest.postForEntity(
                url("/login"),
                new Credentials("noSuch@mail.ru", TANYA_USER.passHash()),
                String.class
        ).getStatusCode().is4xxClientError();
        assertThat(isClientError).isTrue();

        // And can not login with wrong password
        isClientError = rest.postForEntity(
                url("/login"),
                new Credentials(TANYA_USER.email(), "1111111111"),
                String.class
        ).getStatusCode().is4xxClientError();
        assertThat(isClientError).isTrue();

        // Could not register invalid email
        isClientError = rest.postForEntity(
                url("/register"),
                new User("tanya22", "12333333", "emaimail.com", "Tanya", "Pakhomova"),
                String.class
        ).getStatusCode().is4xxClientError();
        assertThat(isClientError).isTrue();

        // Could not register with short password
        isClientError = rest.postForEntity(
                url("/register"),
                new User("tanya22", "123", "email@mail.com", "Tanya", "Pakhomova"),
                String.class
        ).getStatusCode().is4xxClientError();
        assertThat(isClientError).isTrue();

        // Get token to test 'unregister' validation
        String token = rest.postForObject(
                url("/login"),
                new Credentials(TANYA_USER.email(), TANYA_USER.passHash()),
                String.class
        );
        assertThat(token).isNotNull();

        // Wrong email
        ResponseEntity<String> result = rest.exchange(
                url("/unregister/" + "wrongemail"),
                HttpMethod.DELETE,
                authorizedHeader(token),
                String.class
        );
        assertThat(result.getStatusCode().is4xxClientError()).isTrue();

        // With empty token
        result = rest.exchange(
                url("/unregister/" + TANYA_USER.email()),
                HttpMethod.DELETE,
                authorizedHeader(""),
                String.class
        );
        assertThat(result.getStatusCode().is4xxClientError()).isTrue();

        // Valid unregister
        result = rest.exchange(
                url("/unregister/" + TANYA_USER.email()),
                HttpMethod.DELETE,
                authorizedHeader(token),
                String.class
        );
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }



    @NotNull
    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}