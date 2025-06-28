package com.filesharing.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public static String encryptPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }
        return encoder.encode(password);
    }
    
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
}