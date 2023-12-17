package com.tpakhomova.tms.data;

import java.util.List;

public class Task {
    private final String header;
    private final String description;
    private final Status status;
    private final Priority priority;
    private final User author;
    private final User assignee;
    private final List<Comment> comments;

    public Task(String header, String description, Status status, Priority priority, User author, User assignee, List<Comment> comments) {
        this.header = header;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.author = author;
        this.assignee = assignee;
        this.comments = comments;
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

    public User getAuthor() {
        return author;
    }

    public User getAssignee() {
        return assignee;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
