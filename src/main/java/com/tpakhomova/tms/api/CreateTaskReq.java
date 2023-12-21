package com.tpakhomova.tms.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateTaskReq(@NotBlank String header, String description, Status status, Priority priority,
                            @Email String authorEmail, @Email String assigneeEmail) {
}
