package com.ntn.culinary.validator;

import com.ntn.culinary.request.PermissionRequest;

import java.util.HashMap;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;
import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

public class PermissionRequestValidator {
    public Map<String, String> validate(PermissionRequest request, boolean isUpdate) {
        Map<String, String> errors = new HashMap<>();

        if (isUpdate && isNotExistId(request.getId())) {
            errors.put("id", "ID is required and must exist");
        }

        if (isNullOrEmpty(request.getName())) {
            errors.put("name", "Name is required and must exist");
        }

        if (isNullOrEmpty(request.getDescription())) {
            errors.put("description", "Description is required and must exist");
        }

        return errors;
    }
}
