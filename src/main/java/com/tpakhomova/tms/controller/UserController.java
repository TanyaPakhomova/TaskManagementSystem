package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.Credentials;
import com.tpakhomova.tms.api.User;
import com.tpakhomova.tms.security.JwtService;
import com.tpakhomova.tms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Users", description = "Users management APIs.")
@RestController
public class UserController {
    private final UserService userService;

    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    @Operation(summary = "Register a new user by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid user supplied",
                    content = @Content),
            })
    @PostMapping("register")
    String register(@RequestBody @Valid User user) {
        boolean createResult = userService.createUser(new com.tpakhomova.tms.data.User(
           null, user.username(), user.password(), user.email(), user.firstName(), user.lastName()
        ));

        if (createResult) {
            return "Registered successfully";
        } else  {
            throw new InvalidCredentialsException();
        }
    }

    @Operation(summary = "Unregister a new user by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User unregistered"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("unregister/{email}")
    void unregister(@PathVariable @Valid @Email String email) {
        if (userService.deleteUser(email)) {
            return;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Login by email and password. Returns JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in. JWT returned.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid credentials.",
                    content = @Content),
    })
    @PostMapping("login")
    String login(@RequestBody @Valid Credentials credentials) {
        if (userService.checkEmailAndPassword(credentials.email(), credentials.password())) {
            return jwtService.generateToken(userService.findUser(credentials.email()));
        } else {
            throw new InvalidCredentialsException();
        }
    }
}

