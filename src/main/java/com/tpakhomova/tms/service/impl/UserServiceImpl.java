package com.tpakhomova.tms.service.impl;

import com.tpakhomova.tms.data.User;
import com.tpakhomova.tms.persistence.UserRepository;
import com.tpakhomova.tms.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceImpl implements UserService {
   private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUser(String email) {
        return userRepository.findByEmail(email).get();
    }

    @Override
    public boolean createUser(User user) {
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteUser(String email) {
        userRepository.deleteByEmail(email);
        return true;
    }
}
