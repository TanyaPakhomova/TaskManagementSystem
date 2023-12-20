package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.Credentials;
import com.tpakhomova.tms.api.User;
import com.tpakhomova.tms.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    String hello(@RequestBody @Valid Credentials credentials) {
        if (userService.checkEmailAndPassword(credentials.email(), credentials.password())) {
            return "Token";
        } else {
            throw new InvalidCredentialsException();
        }
    }
}

