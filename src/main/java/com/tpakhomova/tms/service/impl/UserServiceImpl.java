package com.tpakhomova.tms.service.impl;

import com.tpakhomova.tms.data.User;
import com.tpakhomova.tms.persistence.CommentsRepository;
import com.tpakhomova.tms.persistence.TaskRepository;
import com.tpakhomova.tms.persistence.data.UserEntity;
import com.tpakhomova.tms.persistence.UserRepository;
import com.tpakhomova.tms.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {
   private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUser(String email) {
        Optional<UserEntity> ue = userRepository.findByEmail(email);

        return ue.map(this::convertUserEntity).orElse(null);
    }

    @Override
    public boolean createUser(User user) {
        try {
            userRepository.save(convertUser(user));
        } catch (Exception ex) { // probably the user with such email or username already exists
            return false;
        }

        userRepository.flush();
        return true;
    }

    @Override
    @Transactional
    public boolean deleteUser(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return false;
        }

        userRepository.deleteByEmail(email);
        return true;
    }

    @Override
    public boolean checkEmailAndPassword(String email, String password) {
        User user = findUser(email);
        if (user == null) {
            return false;
        }

        if (user.getPassHash().equals(password)) { // todo: use hash instead of password
            return true;
        }

        return false;
    }

    private User convertUserEntity(UserEntity ue) {
        return new User(
                ue.getId(), ue.getUsername(), ue.getPassHash(), ue.getEmail(),
                ue.getFirstName(), ue.getLastName()
        );
    }

    private UserEntity convertUser(User user) {
        var ue = new UserEntity();
        ue.setId(user.getId());
        ue.setUsername(user.getUsername());
        ue.setEmail(user.getEmail());
        ue.setFirstName(user.getFirstName());
        ue.setLastName(user.getLastName());
        ue.setPassHash(user.getPassHash());

        return ue;
    }
}
