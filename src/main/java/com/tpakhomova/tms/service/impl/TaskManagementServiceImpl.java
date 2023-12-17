package com.tpakhomova.tms.service.impl;

import com.tpakhomova.tms.data.Comment;
import com.tpakhomova.tms.data.Status;
import com.tpakhomova.tms.data.Task;
import com.tpakhomova.tms.service.TaskManagementService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagementServiceImpl implements TaskManagementService {
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Comment> comments = new HashMap<>();

    @Override
    public Task findTask(Long id) {
        return tasks.get(id);
    }

    @Override
    public boolean createTask(Task task) {
        if (tasks.get(task.getId()) != null) {
            return false;
        }

        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean deleteTask(Long id) {
        return tasks.remove(id) != null;
    }

    @Override
    public boolean editTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            return false;
        }

        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean changeStatus(Long id, Status status) {
        if (tasks.get(id) == null) {
            return false;
        }

        Task taskToChangeStatus = tasks.get(id);
        Task taskWithChangedStatus = new Task(
                taskToChangeStatus.getId(),
                taskToChangeStatus.getHeader(),
                taskToChangeStatus.getDescription(),
                status,
                taskToChangeStatus.getPriority(),
                taskToChangeStatus.getAuthorEmail(),
                taskToChangeStatus.getAssigneeEmail()
        );

        return editTask(taskWithChangedStatus);
    }

    @Override
    public boolean assign(Long taskId, String assigneeEmail) {
        return false; //todo
    }

    @Override
    public boolean addComment(Comment comment) {
        if (comments.get(comment.getCommentId()) != null) {
            return false;
        }

        if (tasks.get(comment.getTaskId()) == null) {
            return false;
        }

        comments.put(comment.getCommentId(), comment);
        return true;
    }

    @Override
    public List<Comment> findCommentsForTask(Long id) {
        if (tasks.get(id) == null) {
            return null;
        }

        List<Comment> result = new ArrayList<>();
        for (var c: comments.values()) {
            if (c.getTaskId().equals(id)) {
                result.add(c);
            }
        }

        return result;
    }

    @Override
    public boolean deleteComment(Long commentId) {
        if (comments.get(commentId) == null) {
            return false;
        }

        comments.remove(commentId);
        return true;
    }
}
