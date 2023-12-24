package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;
import java.util.Objects;

@Schema(name= "Comment for task")
public record Comment (
        @NotBlank Long commentId,
        @NotBlank Long taskId,
        @NotBlank @Email String authorEmail,
        @NotBlank String text,
        Timestamp createdAt) {}
