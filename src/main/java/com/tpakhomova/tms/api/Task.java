package com.tpakhomova.tms.api;

import java.util.Objects;

public class Task {
    private final Long id;
    private final String header;
    private final String description;
    private final Status status;
    private final Priority priority;
    private final String authorEmail;
    private final String assigneeEmail;

    public Task(Long id, String header, String description, Status status, Priority priority, String authorEmail, String assigneeEmail) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.authorEmail = authorEmail;
        this.assigneeEmail = assigneeEmail;
    }

    public Long getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(header, task.header) && Objects.equals(description, task.description) && status == task.status && priority == task.priority && Objects.equals(authorEmail, task.authorEmail) && Objects.equals(assigneeEmail, task.assigneeEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, description, status, priority, authorEmail, assigneeEmail);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", authorEmail='" + authorEmail + '\'' +
                ", assigneeEmail='" + assigneeEmail + '\'' +
                '}';
    }
}
