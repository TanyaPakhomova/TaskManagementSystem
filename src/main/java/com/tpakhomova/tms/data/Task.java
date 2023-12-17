package com.tpakhomova.tms.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.List;
import java.util.Objects;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;
    private String header;
    private String description;
    private Status status;
    private Priority priority;
    private String authorEmail;
    private String assigneeEmail;

    public Task(Long id, String header, String description, Status status, Priority priority, String authorEmail, String assigneeEmail) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.authorEmail = authorEmail;
        this.assigneeEmail = assigneeEmail;
    }

    public Task() {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(header, task.header) && Objects.equals(description, task.description) && status == task.status && priority == task.priority && Objects.equals(authorEmail, task.authorEmail) && Objects.equals(assigneeEmail, task.assigneeEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header, description, status, priority, authorEmail, assigneeEmail);
    }
}
