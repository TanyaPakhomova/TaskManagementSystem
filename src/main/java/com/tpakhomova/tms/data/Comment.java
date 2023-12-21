package com.tpakhomova.tms.data;

import java.sql.Timestamp;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(taskId, comment.taskId) && Objects.equals(authorEmail, comment.authorEmail) && Objects.equals(text, comment.text) && Objects.equals(createdAt, comment.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, authorEmail, text, createdAt);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", taskId=" + taskId +
                ", authorEmail='" + authorEmail + '\'' +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
