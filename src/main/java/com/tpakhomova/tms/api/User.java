package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Schema(name= "User")
public record User (
        @NotBlank
        String username,
        @NotBlank
        String password,
        @Email @NotBlank String email,
        String firstName,
        String lastName) {}
