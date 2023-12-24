package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.*;
import com.tpakhomova.tms.data.Comment;
import com.tpakhomova.tms.data.Priority;
import com.tpakhomova.tms.data.Status;
import com.tpakhomova.tms.service.TaskManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Comments", description = "Comments management APIs.")
@RestController
@RequestMapping("comments")
public class CommentController {

    private final TaskManagementService taskManagementService;


    public CommentController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @Operation(summary = "Add comment to a task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added", content = @Content),
    })
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

    @Operation(summary = "Get all comments for task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments returned",
                    content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentList.class)) }),
    })
    @GetMapping("{taskId}")
    CommentList getByTaskId(@PathVariable Long taskId) {
        var task = taskManagementService.findTask(taskId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<Comment> commentsForTask = taskManagementService.findCommentsForTask(taskId);
        return new CommentList(convert(commentsForTask));
    }

    @Operation(summary = "Delete comment by comment id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted",
                    content = @Content()),
    })
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

