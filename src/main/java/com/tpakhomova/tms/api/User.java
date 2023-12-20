package com.tpakhomova.tms.api;

import jakarta.validation.constraints.Email;

import java.util.Objects;

public record User (String username, String passHash, @Email String email, String firstName, String lastName) {}
