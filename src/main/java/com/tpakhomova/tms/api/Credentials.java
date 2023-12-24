package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name= "Login credentials")
public record Credentials(@Email @NotBlank String email, @NotBlank @Size(min = 8) String password) { }
