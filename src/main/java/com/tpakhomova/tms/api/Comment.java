package com.tpakhomova.tms.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;
import java.util.Objects;

public record Comment (@NotBlank Long commentId, @NotBlank Long taskId, @Email String authorEmail, @NotBlank String text, Timestamp createdAt) {}
