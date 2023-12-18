package com.tpakhomova.tms.persistence.data;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @ManyToOne
    @JoinColumn(name="task_id", nullable=false)
    private TaskEntity task;

    @ManyToOne
    @JoinColumn(name="author_id", nullable = false)
    private UserEntity author;
    @Column(name = "text")
    private String text;
    @Column(name = "created_at")
    private Timestamp createdAt;

    public CommentEntity() {
    }

    public Long getCommentId() {
        return commentId;
    }

    public TaskEntity getTask() {
        return task;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public void setTask(TaskEntity taskEntity) {
        this.task = taskEntity;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentEntity commentEntity = (CommentEntity) o;
        return Objects.equals(commentId, commentEntity.commentId) && Objects.equals(task, commentEntity.task) && Objects.equals(author, commentEntity.author) && Objects.equals(text, commentEntity.text) && Objects.equals(createdAt, commentEntity.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, task, author, text, createdAt);
    }
}
