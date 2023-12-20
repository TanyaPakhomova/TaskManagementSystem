package com.tpakhomova.tms.service;

import com.tpakhomova.tms.data.User;
import com.tpakhomova.tms.persistence.data.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

/**
 * Service that is responcible for CRD operation for {@link UserEntity}.
 */
@Component
public interface UserService {
    /**
     * Finds a user by email.
     *
     * @param email Sting email, for example tanya@gmail.com.
     *              Email is NOT validated by {@link UserService}.
     *
     * @return User if there is a user with such email or {@code null},
     */
    User findUser(String email);

    /**
     * Creates user. The email and username should be unique.
     *
     * @param user User that is NOT validated by {@link UserService}.
     *
     * @return {@code true} if user was created or {@code false} if there is
     * the user with same email or username.
     */
    boolean createUser(User user);

    /**
     * Deletes user by email. User with such email should exist.
     *
     * @param email Sting email, for example tanya@gmail.com.
     *              Email is NOT validated by {@link UserService}.
     *
     * @return {@code true} if user was deleted or {@code false} if there is
     * no the user with provided email.
     */
    boolean deleteUser(String email);

    boolean checkEmailAndPassword(String email, String password);
}
