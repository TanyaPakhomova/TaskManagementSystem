package com.tpakhomova.tms.service;

import com.tpakhomova.tms.data.Comment;
import com.tpakhomova.tms.data.Status;
import com.tpakhomova.tms.data.Task;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Task Management Service. Performs main CRUD operations on {@code Task} and {@code Comment}.
 */
@Component
public interface TaskManagementService {
    /**
     * Finds task by id.
     *
     * @param id task id.
     * @return task of {@code null} if there is no task with such id.
     */
    Task findTask(Long id);

    /**
     * Creates task.
     *
     * @return {@code true} if task was created or {@code false} if there is such task.
     */
    boolean createTask(Task task);

    /**
     * Deletes task by id.
     * @param id task id.
     * @return {@code true} if task was deleted or {@code false} if there no task with such id.
     */

    boolean deleteTask(Long id);

    /**
     * Edits existing task.
     *
     * @param task updated task with all fields filled.
     * @return {@code true} if task was edited or {@code false} if there no such task.
     */
    boolean editTask(Task task);

    /**
     * Changes task status.
     *
     * @param id task id.
     * @param status new status.
     * @return {@code true} if status was updated or {@code false} if there no task with such id.
     */
    boolean changeStatus(Long id, Status status);

    /**
     * Assigns task with given id onto user with given email.
     *
     * @param taskId task id.
     * @param assigneeEmail user email.
     *
     * @return {@code true} if task was assigned or {@code false} if there is no such task or user.
     */
    boolean assign(Long taskId, String assigneeEmail);

    /**
     * Adds comment to the task. Given comment should contain existing user email and task id.
     *
     * @return {@code true} if comment was added or {@code false} if there is no task or user.
     */
    boolean addComment(Comment comment);

    /**
     * Find all comments for task.
     *
     * @param id task id.
     * @return List of comments or empty list if there is no comments for task
     * or {@code null} if there is no task with given id.
     */
    List<Comment> findCommentsForTask(Long id);

    /**
     * Deletes comment by id.
     *
     * @param commentId comment id.
     * @return {@code true} if comment was deleted of {@code false} if there is no such comment.
     */
    boolean deleteComment(Long commentId);
}
