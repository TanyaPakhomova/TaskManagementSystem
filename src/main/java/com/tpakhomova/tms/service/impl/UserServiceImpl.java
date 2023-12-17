package com.tpakhomova.tms.service.impl;

import com.tpakhomova.tms.data.User;
import com.tpakhomova.tms.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final List<User> users = new ArrayList<>();

    @Override
    public User findUser(String email) {
        for (var u: users) {
            if (u.getEmail().equals(email)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public boolean createUser(User user) {
        users.add(user);
        return true;
    }

    @Override
    public boolean deleteUser(String email) {
        for (var u: users) {
            if (u.getEmail().equals(email)) {
                users.remove(u);
                return true;
            }
        }

        return false;
    }
}
