package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name= "Task")
public record Task(
        @NotBlank Long id,
        @NotBlank String header,
        String description,
        Status status,
        Priority priority,
        @Email @NotBlank String authorEmail,
        @Email String assigneeEmail) {
}
