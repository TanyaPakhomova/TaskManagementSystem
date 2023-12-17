package com.tpakhomova.tms.data;

import java.util.List;

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
}
