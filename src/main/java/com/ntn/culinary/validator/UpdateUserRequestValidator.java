package com.ntn.culinary.validator;

import com.ntn.culinary.model.User;
import com.ntn.culinary.request.UserRequest;
import com.ntn.culinary.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserRequestValidator {
    public Map<String, String> validate(UserRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (ValidationUtils.isNotExistId(request.getId())) {
            errors.put("id", "ID is required");
        }

        if (request.getEmail() != null && ValidationUtils.isNullOrEmpty(request.getEmail())) {
            errors.put("email", "Email must not be empty");
        } else if (request.getEmail() != null && !ValidationUtils.isValidEmail(request.getEmail())) {
            errors.put("email", "Invalid email format");
        }

        if (request.getFirstName() != null && ValidationUtils.isNullOrEmpty(request.getFirstName())) {
            errors.put("firstName", "First name must not be empty");
        }

        if (request.getLastName() != null && ValidationUtils.isNullOrEmpty(request.getLastName())) {
            errors.put("lastName", "Last name must not be empty");
        }

        if (request.getPhone() != null && ValidationUtils.isNullOrEmpty(request.getPhone())) {
            errors.put("phone", "Phone must not be empty");
        } else if (request.getPhone() != null && !ValidationUtils.isValidPhone(request.getPhone())) {
            errors.put("phone", "Invalid phone format");
        }

        if (request.getAvatar() == null || request.getAvatar().getSize() == 0) {
            errors.put("image", "Image is required");
        }

        return errors;
    }
}
