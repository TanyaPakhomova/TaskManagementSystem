package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.*;
import com.tpakhomova.tms.data.Comment;
import com.tpakhomova.tms.data.Priority;
import com.tpakhomova.tms.data.Status;
import com.tpakhomova.tms.service.TaskManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("comments")
public class CommentController {

    private final TaskManagementService taskManagementService;


    public CommentController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @PostMapping("{taskId}")
    String create(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid String text) {

       Long commentId = taskManagementService.addComment(convert(taskId, userDetails.getUsername(), text));
       return commentId.toString();
    }

    private Comment convert(Long taskId, String authorEmail, String text) {
        return new Comment(null, taskId, authorEmail, text, Timestamp.from(Instant.now()));
    }

    @GetMapping("{taskId}")
    CommentList getByTaskId(@PathVariable Long taskId) {
        var task = taskManagementService.findTask(taskId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<Comment> commentsForTask = taskManagementService.findCommentsForTask(taskId);
        return new CommentList(convert(commentsForTask));
    }

    @DeleteMapping("{commentId}")
    void deleteById(@PathVariable Long commentId) {
        taskManagementService.deleteComment(commentId);
    }

    private List<com.tpakhomova.tms.api.Comment> convert(List<Comment> commentsForTask) {
        return commentsForTask.stream()
                .map(comment -> new com.tpakhomova.tms.api.Comment(
                        comment.getCommentId(),
                        comment.getTaskId(),
                        comment.getAuthorEmail(),
                        comment.getText(),
                        comment.getCreatedAt()
                        ))
                .toList();
    }
}

