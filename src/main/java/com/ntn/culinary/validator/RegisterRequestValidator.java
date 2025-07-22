package com.ntn.culinary.validator;

import com.ntn.culinary.request.RegisterRequest;

import java.util.HashMap;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

public class RegisterRequestValidator {
    public Map<String, String> validate(RegisterRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (isNullOrEmpty(request.getUsername())) {
            errors.put("username", "Username is required");
        }

        if (isNullOrEmpty(request.getPassword())) {
            errors.put("password", "Password is required");
        }

        return errors;
    }
}
