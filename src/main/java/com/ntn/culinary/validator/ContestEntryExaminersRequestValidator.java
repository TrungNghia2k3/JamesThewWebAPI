package com.ntn.culinary.validator;

import com.ntn.culinary.request.ContestEntryExaminersRequest;
import com.ntn.culinary.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;

public class ContestEntryExaminersRequestValidator {
    public Map<String, String> validate(ContestEntryExaminersRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (isNotExistId(request.getContestEntryId())) {
            errors.put("contestEntryId", "Contest Entry ID is required and must exist");
        }

        if (isNotExistId(request.getExaminerId())) {
            errors.put("examinerId", "Examiner ID is required and must exist");
        }

        if (request.getScore() < 0 || request.getScore() > 10) {
            errors.put("score", "Score must be between 0 and 10");
        }

        return errors;
    }
}
