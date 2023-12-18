package com.tpakhomova.tms.service;

import com.tpakhomova.tms.data.*;
import com.tpakhomova.tms.persistence.CommentsRepository;
import com.tpakhomova.tms.persistence.TaskRepository;
import com.tpakhomova.tms.persistence.UserRepository;
import com.tpakhomova.tms.persistence.data.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class TaskEntityManagementServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @Autowired
    TaskManagementService service;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    CommentsRepository commentsRepository;

    @BeforeEach
    void setUp() {
        var ue = new UserEntity();
        ue.setUsername("testusername");
        ue.setEmail("test@email.ru");
        ue.setPassHash("123");
        ue.setFirstName("First name");
        ue.setLastName("Last name");
        userRepository.save(ue);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        taskRepository.deleteAll();
        taskRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        commentsRepository.deleteAll();
        commentsRepository.flush();
    }

    @Test
    void createTask() {
        var task = task("First task");
        assertNull(service.findTask(222L));

        var createdTaskId = service.createTask(task);
        assertNotNull(createdTaskId);

        var taskFromService= service.findTask(createdTaskId);

        assertNotNull(taskFromService);
        assertEquals(taskFromService, task);
    }

    @Test
    void deleteTask() {
        var task = task("First task");
        assertFalse(service.deleteTask(task.getId()));

        var createdTaskId = service.createTask(task);
        assertNotNull(createdTaskId);

        assertTrue(service.deleteTask(createdTaskId));

        assertNull(service.findTask(createdTaskId));
    }

    @Test
    @Disabled // todo
    void editTask() {
        var task = task("First task");
        var createdTaskId = service.createTask(task);
        assertNotNull(createdTaskId);

        var taskToEdit = task("First task edited");
        assertNotEquals(task, taskToEdit);
        assertTrue(service.editTask(taskToEdit));

        var editedTask = service.findTask(task.getId());
        assertNotNull(editedTask);
        assertEquals(editedTask, task);

        assertFalse(service.editTask(task( "Header")));
    }

    @Test
    void changeStatus() {
        var task = task("Task header");
        var createdTaskId = service.createTask(task);

        assertEquals(task.getStatus(), Status.WAIT);

        assertTrue(service.changeStatus(createdTaskId, Status.PROCESS));
        assertTrue(service.changeStatus(createdTaskId, Status.PROCESS)); // can change the same

        var taskWithChangedStatus = service.findTask(createdTaskId);
        assertEquals(taskWithChangedStatus.getStatus(), Status.PROCESS);

        assertFalse(service.changeStatus(2L, Status.PROCESS));
    }

    @Test
    void assign() {
        // todo:
    }

    @Test
    void addDeleteComment() {
        var task = task( "Task 1");
        var createdTaskId = service.createTask(task);

        var comment = new Comment(null, createdTaskId,
                "test@email.ru", "Comment text", Timestamp.from(Instant.now())
        );
        assertNotNull(createdTaskId);
        Long commentId = service.addComment(comment);
        assertNotNull(commentId);

        List<Comment> commentsForTask = service.findCommentsForTask(createdTaskId);
        assertNotNull(commentsForTask);
        assertEquals(1, commentsForTask.size());
        assertEquals(commentsForTask.get(0), comment);

        assertTrue(service.deleteComment(commentId));
        List<Comment> commentsAfterDelete = service.findCommentsForTask(createdTaskId);
        assertNotNull(commentsAfterDelete);
        assertTrue(commentsAfterDelete.size() == 0);

        // No such task id
        var invalidComment = new Comment(11L, 22L,
                "some@mail.com", "Comment text", Timestamp.from(Instant.now())
        );

        assertNull(service.addComment(invalidComment));
        assertNull(service.findCommentsForTask(22L));
        assertFalse(service.deleteComment(22L));
    }

    Task task(String header) {
        return new Task(null, header, "Test description", Status.WAIT,
                Priority.MEDIUM, "test@email.ru", null
        );
    }
}