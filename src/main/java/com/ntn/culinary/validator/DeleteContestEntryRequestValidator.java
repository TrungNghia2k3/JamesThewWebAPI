package com.ntn.culinary.validator;

import com.ntn.culinary.request.DeleteContestEntryRequest;

import java.util.HashMap;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;
import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

public class DeleteContestEntryRequestValidator {
    public Map<String, String> validate(DeleteContestEntryRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (isNotExistId(request.getId())) {
            errors.put("id", "Contest Entry ID is required");
        }

        if (isNotExistId(request.getContestId())) {
            errors.put("contestId", "Contest ID is required");
        }

        if (isNotExistId(request.getUserId())) {
            errors.put("userId", "User ID is required");
        }

        if (isNullOrEmpty(request.getName())) {
            errors.put("name", "Name is required");
        }

        return errors;
    }
}
