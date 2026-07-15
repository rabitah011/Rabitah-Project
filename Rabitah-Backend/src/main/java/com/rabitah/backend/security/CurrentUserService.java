package com.rabitah.backend.security;

import com.rabitah.backend.common.ApiException;
import com.rabitah.backend.user.AccountStatus;
import com.rabitah.backend.user.User;
import com.rabitah.backend.user.UserRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository users;

    public CurrentUserService(UserRepository users) { this.users = users; }

    public User require(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", "Sign in is required");
        }
        User user = users.findById(UUID.fromString(jwt.getSubject()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_SESSION", "Account no longer exists"));
        Number tokenVersion = jwt.getClaim("tokenVersion");
        if (user.getStatus() != AccountStatus.ACTIVE || tokenVersion == null || user.getTokenVersion() != tokenVersion.intValue()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_SESSION", "Session is no longer active");
        }
        return user;
    }
}
