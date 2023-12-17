package com.tpakhomova.tms.service;

import com.tpakhomova.tms.data.*;
import com.tpakhomova.tms.persistence.CommentsRepository;
import com.tpakhomova.tms.persistence.TaskRepository;
import com.tpakhomova.tms.persistence.UserRepository;
import com.tpakhomova.tms.service.impl.TaskManagementServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TaskManagementServiceTest {
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
        taskRepository.deleteAll();
        commentsRepository.deleteAll();
//        userRepository.deleteAll();
    }

    @Test
    void createTask() {
        var task = task(1L, "First task");
        assertNull(service.findTask(task.getId()));

        assertTrue(service.createTask(task));

        var taskFromService= service.findTask(task.getId());

        assertNotNull(taskFromService);
        assertEquals(taskFromService, task);
    }

    @Test
    void deleteTask() {
        var task = task(1L, "First task");
        assertFalse(service.deleteTask(task.getId()));

        assertTrue(service.createTask(task));

        assertTrue(service.deleteTask(task.getId()));

        assertNull(service.findTask(task.getId()));
    }

    @Test
    void editTask() {
        var task = task(1L, "First task");
        assertTrue(service.createTask(task));

        var taskToEdit = task(1L, "First task edited");
        assertNotEquals(task, taskToEdit);
        assertTrue(service.editTask(taskToEdit));

        var editedTask = service.findTask(task.getId());
        assertNotNull(editedTask);
        assertEquals(editedTask, taskToEdit);

        assertFalse(service.editTask(task(2L, "Header")));
    }

    @Test
    void changeStatus() {
        var task = task(1L, "Task header");
        service.createTask(task);

        assertEquals(task.getStatus(), Status.WAIT);

        assertTrue(service.changeStatus(task.getId(), Status.PROCESS));
        assertTrue(service.changeStatus(task.getId(), Status.PROCESS)); // can change the same

        var taskWithChangedStatus = service.findTask(task.getId());
        assertEquals(taskWithChangedStatus.getStatus(), Status.PROCESS);

        assertFalse(service.changeStatus(2L, Status.PROCESS));
    }

    @Test
    void assign() {
        // todo:
    }

    @Test
    void addDeleteComment() {
        var task = task(1L, "Task 1");
        var comment = new Comment(11L, task.getId(),
                "some@mail.com", "Comment text", Timestamp.from(Instant.now())
        );

        assertTrue(service.createTask(task));
        assertTrue(service.addComment(comment));

        List<Comment> commentsForTask = service.findCommentsForTask(task.getId());
        assertNotNull(commentsForTask);
        assertEquals(1, commentsForTask.size());
        assertEquals(commentsForTask.get(0), comment);

        assertTrue(service.deleteComment(comment.getCommentId()));
        List<Comment> commentsAfterDelete = service.findCommentsForTask(task.getId());
        assertNotNull(commentsAfterDelete);
        assertTrue(commentsAfterDelete.size() == 0);

        // No such task id
        var invalidComment = new Comment(11L, 22L,
                "some@mail.com", "Comment text", Timestamp.from(Instant.now())
        );

        assertFalse(service.addComment(invalidComment));
        assertNull(service.findCommentsForTask(22L));
        assertFalse(service.deleteComment(22L));
    }

    Task task(Long id, String header) {
        return new Task(
                id, header, "Test description", Status.WAIT,
                Priority.MEDIUM, "test@email.ru", null
        );
    }
}