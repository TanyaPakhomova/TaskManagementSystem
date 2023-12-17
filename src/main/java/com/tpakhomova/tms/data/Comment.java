package com.tpakhomova.tms.data;

import java.sql.Timestamp;

public class Comment {
    private final User author;
    private final String text;
    private final Timestamp createdAt;

    public Comment(User author, String text, Timestamp createdAt) {
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    public User getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
