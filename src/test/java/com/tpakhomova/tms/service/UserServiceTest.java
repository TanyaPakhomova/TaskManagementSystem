package com.tpakhomova.tms.service;


import com.tpakhomova.tms.data.User;
import com.tpakhomova.tms.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.checkerframework.checker.signature.qual.CanonicalName;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    void createUser() {
        var user = user("test@mail.ru");

        // User is not created yet
        assertNull(userService.findUser(user.getEmail()));

        var createResult = userService.createUser(user);
        assertTrue(createResult);

        User userFromService = userService.findUser(user.getEmail());
        assertNotNull(userFromService);
        assertEquals(user, userFromService);
    }

    @Test
    void deleteUser() {
        var user = user("test@mail.ru");
        assertFalse(userService.deleteUser(user.getEmail()));

        userService.createUser(user);

        assertNotNull(userService.findUser(user.getEmail()));
        assertTrue(userService.deleteUser(user.getEmail()));
        assertNull(userService.findUser(user.getEmail()));
    }

    @Test
    void deleteCreateFindMany() {
        var user1 = user("test1@mail.ru");
        var user2 = user("test2@mail.ru");

        assertFalse(userService.deleteUser(user1.getEmail()));
        assertFalse(userService.deleteUser(user2.getEmail()));

        assertNull(userService.findUser(user1.getEmail()));
        assertNull(userService.findUser(user2.getEmail()));

        assertTrue(userService.createUser(user1));
        assertTrue(userService.createUser(user2));

        var user1FromService = userService.findUser(user1.getEmail());
        var user2FromService = userService.findUser(user2.getEmail());

        assertNotNull(user1FromService);
        assertNotNull(user2FromService);
        assertEquals(user1FromService, user1);
        assertEquals(user2FromService, user2);

        assertTrue(userService.deleteUser(user1.getEmail()));

        assertNull(userService.findUser(user1.getEmail()));

        user2FromService = userService.findUser(user2.getEmail());
        assertNotNull(user2FromService);
        assertEquals(user2FromService, user2);
    }

    // todo: add corner cases like null, empty strings etc


    User user(String email) {
        return new User(email + "_user", "123", email, "Pavel","Popov");
    }
}