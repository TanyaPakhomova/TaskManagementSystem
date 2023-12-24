package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demo for main functionality: task management and authentication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class DemoTest {
    static final User TANYA_USER = new User("tanya", "123456789", "email@mail.com", "Tanya", "Pakhomova");
    static final User DIMA_USER = new User("dime", "wwwwwwwww", "dima@mail.com", "Dima", "Ivanov");
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;

    @AfterEach
    void tearDown() {
        deleteUser(TANYA_USER);
        deleteUser(DIMA_USER);
    }

    private void deleteUser(User user) {
        String token = rest.postForObject(
                url("/login"),
                new Credentials(user.email(), user.passHash()),
                String.class
        );
        // No such user
        if (token == null) {
            return;
        }

        TaskList allUserTasks = rest.exchange(
                url("/tasks/author/" + user.email()),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                TaskList.class
        ).getBody();

        for (var task: allUserTasks.tasks()) {
            deleteTask(task.id(), token);
        }

        rest.exchange(
                url("/unregister/" + user.email()),
                HttpMethod.DELETE,
                authorized(token),
                String.class
        );
    }

    private void deleteTask(Long id, String token) {
        CommentList comments = rest.exchange(url("/comments/" + id),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                CommentList.class
        ).getBody();

        // No comments for this task
        if (comments == null) {
            return;
        }

        for (var comment: comments.comments()) {
            rest.exchange(
                    url("/comments/" + comment.commentId()),
                    HttpMethod.DELETE,
                    authorized(token),
                    String.class
            );
        }

        rest.exchange(
                url("/tasks/" + id),
                HttpMethod.DELETE,
                authorized(token),
                String.class
        );
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
        assertThat(token).isNotNull();

        ResponseEntity<String> result = rest.exchange(
                url("/unregister/" + TANYA_USER.email()),
                HttpMethod.DELETE,
                authorized(token),
                String.class
        );
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @NotNull
    private static HttpEntity<Object> authorized(String token) {
        HttpHeaders headers = jwtHeader(token);
        return new HttpEntity<>(headers);
    }

    @NotNull
    private static HttpHeaders jwtHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
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
                authorized(token),
                String.class
        );
        assertThat(result.getStatusCode().is4xxClientError()).isTrue();

        // With empty token
        result = rest.exchange(
                url("/unregister/" + TANYA_USER.email()),
                HttpMethod.DELETE,
                authorized(""),
                String.class
        );
        assertThat(result.getStatusCode().is4xxClientError()).isTrue();

        // Valid unregister
        result = rest.exchange(
                url("/unregister/" + TANYA_USER.email()),
                HttpMethod.DELETE,
                authorized(token),
                String.class
        );
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("This is the base test for task management system")
    void taskManagementBaseTest() {
        // Register two users
        assertThat(rest.postForObject(url("/register"), TANYA_USER, String.class))
                .contains("Registered successfully");
        assertThat(rest.postForObject(url("/register"), DIMA_USER, String.class))
                .contains("Registered successfully");

        // Login as tanya
        String token = rest.postForObject(
                url("/login"),
                new Credentials(TANYA_USER.email(), TANYA_USER.passHash()),
                String.class
        );

        // Create task for dima
        CreateTaskReq task1Req = new CreateTaskReq(
                "Dima's first task",
                "You should pass the interview",
                Status.WAIT,
                Priority.HIGH,
                TANYA_USER.email(),
                DIMA_USER.email()
        );
        HttpStatusCode code = rest.exchange(
                url("/tasks"),
                HttpMethod.POST,
                new HttpEntity<>(task1Req, jwtHeader(token)),
                String.class
        ).getStatusCode();
        assertThat(code.is2xxSuccessful()).isTrue();

        // Create task for tanya
        CreateTaskReq task2req = new CreateTaskReq(
                "Tanya's first task",
                "I should pass the interview 100%",
                Status.WAIT,
                Priority.HIGH,
                TANYA_USER.email(),
                TANYA_USER.email()
        );
        code = rest.exchange(
                url("/tasks"),
                HttpMethod.POST,
                new HttpEntity<>(task2req, jwtHeader(token)),
                String.class
        ).getStatusCode();
        assertThat(code.is2xxSuccessful()).isTrue();

        // GET by author
        TaskList taskList = rest.exchange(
                url("/tasks/author/" + TANYA_USER.email()),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                TaskList.class
        ).getBody();

        assertThat(taskList.tasks()).hasSize(2);

        Set<String> authorEmails = taskList.tasks()
                .stream()
                .map(Task::authorEmail)
                .collect(Collectors.toSet());

        // Only tanya is author
        assertThat(authorEmails).contains(TANYA_USER.email());
        assertThat(authorEmails).hasSize(1);

        // GET by assignee for dima
        taskList = rest.exchange(
                url("/tasks/assignee/" + DIMA_USER.email()),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                TaskList.class
        ).getBody();

        assertThat(taskList.tasks()).hasSize(1);
        assertThat(taskList.tasks().get(0).assigneeEmail()).isEqualTo(DIMA_USER.email());

        // GET by assignee for tanya
        taskList = rest.exchange(
                url("/tasks/assignee/" + TANYA_USER.email()),
                HttpMethod.GET,
                authorized(token),
                TaskList.class
        ).getBody();

        assertThat(taskList.tasks()).hasSize(1);
        assertThat(taskList.tasks().get(0).assigneeEmail()).isEqualTo(TANYA_USER.email());

        // Edit task
        Task taskToUpdate = taskList.tasks().get(0);
        var taskIdToUptate = taskToUpdate.id();
        code = rest.exchange(
                url("/tasks/" + taskIdToUptate),
                HttpMethod.PUT,
                new HttpEntity<>(new CreateTaskReq(
                        "New updated header", taskToUpdate.description(),
                        taskToUpdate.status(), taskToUpdate.priority(),
                        taskToUpdate.authorEmail(), taskToUpdate.assigneeEmail()
                ), jwtHeader(token)),
                String.class
        ).getStatusCode();
        assertThat(code.is2xxSuccessful()).isTrue();

        // GET by assignee for dima
        Task updatedTask = rest.exchange(
                url("/tasks/" + taskIdToUptate),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                Task.class
        ).getBody();

        assertThat(updatedTask.header()).isEqualTo("New updated header");

        // Delete task by id
        code = rest.exchange(
                url("/tasks/" + taskIdToUptate),
                HttpMethod.DELETE,
                authorized(token),
                String.class
        ).getStatusCode();
        assertThat(code.is2xxSuccessful()).isTrue();

        code = rest.exchange(
                url("/tasks/" + taskIdToUptate),
                HttpMethod.GET,
                authorized(token),
                Void.class
        ).getStatusCode();

        assertThat(code.is4xxClientError()).isTrue();

        // Change status
        Task taskToUpdateStatus = rest.exchange(
                url("/tasks/author/" + TANYA_USER.email()),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                TaskList.class
        ).getBody().tasks().get(0);

        assertThat(taskToUpdateStatus.status()).isEqualTo(Status.WAIT);

        code = rest.exchange(
                url("/tasks/status/" + taskToUpdateStatus.id()),
                HttpMethod.PUT,
                new HttpEntity<>("PROCESS", jwtHeader(token)),
                Void.class
        ).getStatusCode();
        assertThat(code.is2xxSuccessful()).isTrue();

        // Update priority
        code = rest.exchange(
                url("/tasks/priority/" + taskToUpdateStatus.id()),
                HttpMethod.PUT,
                new HttpEntity<>("LOW", jwtHeader(token)),
                Void.class
        ).getStatusCode();
        assertThat(code.is2xxSuccessful()).isTrue();

        Task changedTask = rest.exchange(
                url("/tasks/" + taskToUpdateStatus.id()),
                HttpMethod.GET,
                authorized(token),
                Task.class
        ).getBody();

        assertThat(changedTask.status()).isEqualTo(Status.PROCESS);
        assertThat(changedTask.priority()).isEqualTo(Priority.LOW);
    }

    @Test
    void authorization() {
        // Register two users
        rest.postForObject(url("/register"), TANYA_USER, String.class);
        rest.postForObject(url("/register"), DIMA_USER, String.class);

        // Login as tanya
        String token = rest.postForObject(
                url("/login"),
                new Credentials(TANYA_USER.email(), TANYA_USER.passHash()),
                String.class
        );

        // Create task for herself
        CreateTaskReq task1Req = new CreateTaskReq(
                "Tanya's task",
                "You should pass the interview",
                Status.WAIT,
                Priority.HIGH,
                TANYA_USER.email(),
                TANYA_USER.email()
        );
        rest.exchange(
                url("/tasks"),
                HttpMethod.POST,
                new HttpEntity<>(task1Req, jwtHeader(token)),
                String.class
        );

        // Login as dima
        token = rest.postForObject(
                url("/login"),
                new Credentials(DIMA_USER.email(), DIMA_USER.passHash()),
                String.class
        );

        // Change status
        Task task = rest.exchange(
                url("/tasks/author/" + TANYA_USER.email()),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                TaskList.class
        ).getBody().tasks().get(0);

        var code = rest.exchange(
                url("/tasks/status/" + task.id()),
                HttpMethod.PUT,
                new HttpEntity<>("PROCESS", jwtHeader(token)),
                Void.class
        ).getStatusCode();
        assertThat(code.is4xxClientError()).isTrue();

        // Can not delete task that is not created by current user
        code = rest.exchange(
                url("/tasks/" + task.id()),
                HttpMethod.DELETE,
                authorized(token),
                String.class
        ).getStatusCode();
        assertThat(code.is4xxClientError()).isTrue();
    }

    @Test
    void comments() {
        // Register two users
        rest.postForObject(url("/register"), TANYA_USER, String.class);
        rest.postForObject(url("/register"), DIMA_USER, String.class);

        // Login as tanya
        String token = rest.postForObject(
                url("/login"),
                new Credentials(TANYA_USER.email(), TANYA_USER.passHash()),
                String.class
        );

        // Create task for dima
        CreateTaskReq task1Req = new CreateTaskReq(
                "Dima's first task",
                "You should pass the interview",
                Status.WAIT,
                Priority.HIGH,
                TANYA_USER.email(),
                DIMA_USER.email()
        );
        rest.exchange(
                url("/tasks"),
                HttpMethod.POST,
                new HttpEntity<>(task1Req, jwtHeader(token)),
                String.class
        );

        // Create task for tanya
        CreateTaskReq task2req = new CreateTaskReq(
                "Tanya's first task",
                "I should pass the interview 100%",
                Status.WAIT,
                Priority.HIGH,
                TANYA_USER.email(),
                TANYA_USER.email()
        );
        rest.exchange(
                url("/tasks"),
                HttpMethod.POST,
                new HttpEntity<>(task2req, jwtHeader(token)),
                String.class
        );

        Task task = rest.exchange(
                url("/tasks/author/" + TANYA_USER.email()),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                TaskList.class
        ).getBody().tasks().get(0);

        // Add comment
        var code = rest.postForEntity(
                url("/comments/" + task.id()),
                new HttpEntity<>("I really like it!", jwtHeader(token)),
                String.class
        ).getStatusCode();
        assertThat(code.is2xxSuccessful()).isTrue();

        var comments = rest.exchange(
                url("/comments/" + task.id()),
                HttpMethod.GET,
                new HttpEntity<>(jwtHeader(token)),
                CommentList.class
        ).getBody().comments();

        assertThat(comments.size()).isEqualTo(1);

    }

    @NotNull
    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}