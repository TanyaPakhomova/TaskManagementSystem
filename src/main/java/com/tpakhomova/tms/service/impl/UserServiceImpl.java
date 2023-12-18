package com.tpakhomova.tms.service.impl;

import com.tpakhomova.tms.data.User;
import com.tpakhomova.tms.persistence.CommentsRepository;
import com.tpakhomova.tms.persistence.TaskRepository;
import com.tpakhomova.tms.persistence.data.UserEntity;
import com.tpakhomova.tms.persistence.UserRepository;
import com.tpakhomova.tms.service.UserService;
import org.springframework.stereotype.Component;

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
        userRepository.save(convertUser(user));
        userRepository.flush();
        return true;
    }

    @Override
    public boolean deleteUser(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return false;
        }

        userRepository.deleteByEmail(email);
        return true;
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
