package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name= "Create task request")
public record CreateTaskReq(
        @NotBlank String header,
        String description,
        @NotBlank
        Status status,
        @NotBlank
        Priority priority,
        @Email @NotBlank String authorEmail,
        @Email String assigneeEmail) {
}
