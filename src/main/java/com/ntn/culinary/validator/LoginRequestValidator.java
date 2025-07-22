package com.ntn.culinary.validator;

import com.ntn.culinary.request.LoginRequest;

import java.util.Map;

public class LoginRequestValidator {
    public Map<String, String> validate(LoginRequest request) {
        Map<String, String> errors = new java.util.HashMap<>();

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            errors.put("username", "Username is required");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            errors.put("password", "Password is required");
        }

        return errors;
    }
}
