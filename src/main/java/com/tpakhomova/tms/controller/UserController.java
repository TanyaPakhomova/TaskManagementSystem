package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.Credentials;
import com.tpakhomova.tms.api.User;
import com.tpakhomova.tms.security.JwtService;
import com.tpakhomova.tms.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("register")
    String register(@RequestBody @Valid User user) {
        boolean createResult = userService.createUser(new com.tpakhomova.tms.data.User(
           null, user.username(), user.passHash(), user.email(), user.firstName(), user.lastName()
        ));

        if (createResult) {
            return "Registered successfully";
        } else  {
            throw new InvalidCredentialsException();
        }
    }

    @DeleteMapping("unregister/{email}")
    void unregister(@PathVariable @Valid @Email String email) {
        if (userService.deleteUser(email)) {
            return;
        }

        throw new RuntimeException();
    }

    @PostMapping("login")
    String login(@RequestBody @Valid Credentials credentials) {
        if (userService.checkEmailAndPassword(credentials.email(), credentials.password())) {
            return jwtService.generateToken(userService.findUser(credentials.email()));
        } else {
            throw new InvalidCredentialsException();
        }
    }
}

