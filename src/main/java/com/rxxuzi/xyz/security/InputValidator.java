package com.rxxuzi.xyz.security;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class InputValidator {

    private static final int MAX_POST_LENGTH = 280;
    private static final int MAX_USERNAME_LENGTH = 30;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    public boolean isValidPostContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        if (content.length() > MAX_POST_LENGTH) {
            return false;
        }

        return true;
    }

    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            return false;
        }

        return USERNAME_PATTERN.matcher(username).matches();
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    public String trimAndClean(String input) {
        if (input == null) {
            return null;
        }

        return input.trim();
    }
}