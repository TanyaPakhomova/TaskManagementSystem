package com.tpakhomova.tms.security;

import com.tpakhomova.tms.data.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractEmail(String token);

    String generateToken(User user);

    boolean isTokenValid(String token, UserDetails user);
}
