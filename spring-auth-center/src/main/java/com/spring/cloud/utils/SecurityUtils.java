package com.spring.cloud.utils;

import com.spring.cloud.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class SecurityUtils {
    public static User currentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (authentication == null) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            return (User) principal;
        } catch (Exception e) {
            return null;
        }
    }

    public static String encode(CharSequence rawPassword) {
        String salt=BCrypt.gensalt();
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

}
