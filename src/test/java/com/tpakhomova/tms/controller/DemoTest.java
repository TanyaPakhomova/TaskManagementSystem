package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.Credentials;
import com.tpakhomova.tms.api.User;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

    @BeforeEach
    void setUp() {
        clearTasks();
        clearUsers();
    }

    private void clearUsers() {
        rest.delete(url("/unregister/" + TANYA_USER.email()));
    }

    private void clearTasks() {
        // rest.delete(url("/tasks/remove"));
    }

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
        assertThat(token).isEqualTo("Token");
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
    }



    @NotNull
    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}