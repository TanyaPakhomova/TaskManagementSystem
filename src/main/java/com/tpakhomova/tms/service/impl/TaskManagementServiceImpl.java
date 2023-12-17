package com.tpakhomova.tms.service.impl;

import com.tpakhomova.tms.data.Comment;
import com.tpakhomova.tms.data.Status;
import com.tpakhomova.tms.data.Task;
import com.tpakhomova.tms.persistence.CommentsRepository;
import com.tpakhomova.tms.persistence.TaskRepository;
import com.tpakhomova.tms.persistence.UserRepository;
import com.tpakhomova.tms.service.TaskManagementService;
import com.tpakhomova.tms.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskManagementServiceImpl implements TaskManagementService {
    private final UserService userService;
    private final CommentsRepository commentsRepository;
    private final TaskRepository taskRepository;

    public TaskManagementServiceImpl(UserService userService, CommentsRepository commentsRepository, TaskRepository taskRepository) {
        this.userService = userService;
        this.commentsRepository = commentsRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Task findTask(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createTask(Task task) {
        if (findTask(task.getId()) != null) {
            return false;
        }
        taskRepository.save(task);
        return true;
    }

    @Override
    public boolean deleteTask(Long id) {
        if (findTask(id) == null) {
            return false;
        }

        taskRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean editTask(Task task) {
        if (findTask(task.getId()) == null) {
            return false;
        }

        taskRepository.save(task);
        return true;
    }

    @Override
    public boolean changeStatus(Long id, Status status) {
        if (findTask(id) == null) {
            return false;
        }

        Task taskToChangeStatus = taskRepository.findById(id).orElse(null);
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
        if (commentsRepository.findById(comment.getCommentId()).orElse(null) != null) {
            return false;
        }

        if (findTask(comment.getTaskId()) == null) {
            return false;
        }

        commentsRepository.save(comment);
        return true;
    }

    @Override
    public List<Comment> findCommentsForTask(Long id) {
        if (findTask(id) == null) {
            return null;
        }

        List<Comment> result = new ArrayList<>();
        for (var c: commentsRepository.findAll()) {
            if (c.getTaskId().equals(id)) {
                result.add(c);
            }
        }

        return result;
    }

    @Override
    public boolean deleteComment(Long commentId) {
        if (commentsRepository.findById(commentId).orElse(null) == null) {
            return false;
        }

        commentsRepository.deleteById(commentId);
        return true;
    }
}
