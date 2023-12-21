package com.tpakhomova.tms.service.impl;

import com.tpakhomova.tms.data.Comment;
import com.tpakhomova.tms.data.Task;
import com.tpakhomova.tms.persistence.CommentsRepository;
import com.tpakhomova.tms.persistence.TaskRepository;
import com.tpakhomova.tms.persistence.UserRepository;
import com.tpakhomova.tms.persistence.data.*;
import com.tpakhomova.tms.service.TaskManagementService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TaskManagementServiceImpl implements TaskManagementService {
    private final UserRepository userRepository;
    private final CommentsRepository commentsRepository;
    private final TaskRepository taskRepository;

    public TaskManagementServiceImpl(UserRepository userRepository, CommentsRepository commentsRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.commentsRepository = commentsRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Task findTask(Long id) {
        if (id == null) {
            return null;
        }

        return taskRepository.findById(id)
                .map(this::convertTaskEntity)
                .orElse(null);
    }

    @Override
    public List<Task> findTasksByAuthor(String authorEmail) {
        return taskRepository.findByAuthorEmail(authorEmail)
                .stream()
                .map(this::convertTaskEntity)
                .toList();
    }

    @Override
    public List<Task> findTasksByAssignee(String assigneeEmail) {
        return taskRepository.findByAssigneeEmail(assigneeEmail)
                .stream()
                .map(this::convertTaskEntity)
                .toList();
    }

    @Override
    public  Long createTask(Task task) {
        if (findTask(task.getId()) != null) {
            return null;
        }
        return taskRepository.save(convertTask(task)).getId();
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

        var te = convertTask(task);
        taskRepository.save(te);

        return true;
    }

    private TaskEntity convertTask(Task task) {
        var author = userRepository.findByEmail(task.getAuthorEmail()).get();
        var assignee = userRepository.findByEmail(task.getAssigneeEmail()).orElse(null);
        var comments = commentsRepository.findByTaskId(task.getId());

        var te = new TaskEntity();
        te.setId(task.getId());
        te.setAssignee(assignee);
        te.setAuthor(author);
        te.setPriority(Priority.valueOf(task.getPriority().name()));
        te.setStatus(Status.valueOf(task.getStatus().name()));
        te.setComments(comments);
        te.setHeader(task.getHeader());
        te.setDescription(task.getDescription());
        return te;
    }

    private Task convertTaskEntity(TaskEntity te) {
        String assigneeEmail = te.getAssignee() == null ? null: te.getAssignee().getEmail();
        return new Task(te.getId(), te.getHeader(), te.getDescription(), com.tpakhomova.tms.data.Status.valueOf(te.getStatus().name()), com.tpakhomova.tms.data.Priority.valueOf(te.getPriority().name()), te.getAuthor().getEmail(), assigneeEmail);
    }

    @Override
    public boolean changeStatus(Long id, com.tpakhomova.tms.data.Status status) {
        if (findTask(id) == null) {
            return false;
        }

        TaskEntity taskEntityToChangeStatus = taskRepository.findById(id).orElse(null);

        taskEntityToChangeStatus.setStatus(Status.valueOf(status.name()));
        taskRepository.save(taskEntityToChangeStatus);

        return true;
    }

    @Override
    public boolean changePriority(Long id, com.tpakhomova.tms.data.Priority priority) {
        if (findTask(id) == null) {
            return false;
        }

        TaskEntity taskEntityToChangeStatus = taskRepository.findById(id).orElse(null);

        taskEntityToChangeStatus.setPriority(Priority.valueOf(priority.name()));
        taskRepository.save(taskEntityToChangeStatus);

        return true;
    }


    @Override
    public boolean assign(Long taskId, String assigneeEmail) {
        return false; //todo
    }

    @Override
    public Long addComment(Comment comment) {
//        if (commentsRepository.findById(comment.getCommentId()).orElse(null) != null) {
//            return false;
//        }
//
//        if (findTask(comment.getTaskId()) == null) {
//            return false;
//        }

        var author = userRepository.findByEmail(comment.getAuthorEmail()).orElse(null);
        if (author == null) {
            return null;
        }

        var task = taskRepository.findById(comment.getTaskId()).orElse(null);
        if (task == null) {
            return null;
        }

        var ce = new CommentEntity();
        ce.setAuthor(author);
        ce.setTask(task);
        ce.setText(comment.getText());
        ce.setCreatedAt(comment.getCreatedAt());

        return commentsRepository.save(ce).getCommentId();
    }

    @Override
    public List<Comment> findCommentsForTask(Long id) {
        if (findTask(id) == null) {
            return null;
        }

        List<Comment> result = new ArrayList<>();
        for (var ce : commentsRepository.findAll()) {
            if (ce.getTask().getId().equals(id)) {
                var comment = new Comment(ce.getCommentId(), ce.getTask().getId(), ce.getAuthor().getEmail(), ce.getText(), ce.getCreatedAt());
                result.add(comment);
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
