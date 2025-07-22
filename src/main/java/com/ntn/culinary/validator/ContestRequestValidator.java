package com.ntn.culinary.validator;

import com.ntn.culinary.request.ContestRequest;

import java.util.HashMap;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;
import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

public class ContestRequestValidator {
    public Map<String, String> validate(ContestRequest request, boolean isUpdate) {
        Map<String, String> errors = new HashMap<>();

        if (isUpdate && isNotExistId(request.getId())) {
            errors.put("id", "ID is required for update");
        }

        if (isNullOrEmpty(request.getHeadline())) {
            errors.put("headline", "Headline is required");
        }

        if (isNullOrEmpty(request.getArticleBody())) {
            errors.put("articleBody", "Article body is required");
        }

        if (isNullOrEmpty(request.getDescription())) {
            errors.put("description", "Description is required");
        }

        if (isNullOrEmpty(request.getPrize())) {
            errors.put("prize", "Prize is required");
        }

        if (isNullOrEmpty(request.getAccessRole())) {
            errors.put("accessRole", "Access role is required");
        }

        // Kiểm tra phải có ít nhất một hình ảnh
        if (request.getContestImages() == null || request.getContestImages().isEmpty()) {
            errors.put("contestImages", "At least one contest image is required");
        } else {
            for (int i = 0; i < request.getContestImages().size(); i++) {
                if (request.getContestImages().get(i).getImage().getSize() == 0) {
                    errors.put("contestImages[" + i + "].imageUrl", "Image URL is required for image " + (i + 1));
                }
            }
        }

        return errors;
    }
}
