package com.ntn.culinary.validator;

import com.ntn.culinary.request.CommentRequest;

import java.util.HashMap;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;
import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

public class CommentRequestValidator {
    public Map<String, String> validate(CommentRequest commentRequest, boolean isUpdate) {
        Map<String, String> errors = new HashMap<>();

        if (isUpdate) {
            if (isNotExistId(commentRequest.getId())) {
                errors.put("id", "ID is required for update");
            }
        }

        if (isNotExistId(commentRequest.getUserId())) {
            errors.put("userId", "User ID is required");
        }

        if (isNotExistId(commentRequest.getRecipeId())) {
            errors.put("recipeId", "Recipe ID is required");
        }

        if (isNullOrEmpty(commentRequest.getContent())) {
            errors.put("content", "Content is required");
        }

        if (commentRequest.getRating() < 1 || commentRequest.getRating() > 5) {
            errors.put("rating", "Rating must be between 1 and 5");
        }

        return errors;
    }
}
