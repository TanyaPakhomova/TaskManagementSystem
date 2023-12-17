package com.tpakhomova.tms.data;

import java.sql.Timestamp;

public class Comment {
    private final Long commentId;
    private final Long taskId;
    private final String authorEmail;
    private final String text;
    private final Timestamp createdAt;

    public Comment(Long commentId, Long taskId, String authorEmail, String text, Timestamp createdAt) {
        this.commentId = commentId;
        this.taskId = taskId;
        this.authorEmail = authorEmail;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getText() {
        return text;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
