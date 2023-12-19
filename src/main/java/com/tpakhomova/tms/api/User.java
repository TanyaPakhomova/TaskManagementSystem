package com.tpakhomova.tms.api;

import java.util.Objects;

public record User (Long id, String username, String passHash, String email, String firstName, String lastName) {}
