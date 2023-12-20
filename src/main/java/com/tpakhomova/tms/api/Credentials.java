package com.tpakhomova.tms.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Credentials(@Email String email, @NotBlank @Size(min = 8) String password) { }
