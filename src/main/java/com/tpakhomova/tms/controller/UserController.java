package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.data.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("hello")
    String hello() {
        return "Hello";
    }
}

