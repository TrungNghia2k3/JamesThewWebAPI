package com.ntn.culinary.validator;

import com.ntn.culinary.model.ContestEntryInstruction;
import com.ntn.culinary.request.ContestEntryRequest;
import com.ntn.culinary.utils.ValidationUtils;

import javax.servlet.http.Part;
import java.util.HashMap;
import java.util.Map;

import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;
import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

public class ContestEntryRequestValidator {
    public Map<String, String> validateContestEntryRequest(ContestEntryRequest request, Part imagePart, boolean isUpdate) {

        Map<String, String> errors = new HashMap<>();

        if (isUpdate && isNotExistId(request.getId())) {
            errors.put("id", "ID should not be provided for new contest entries");
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

        if (isNullOrEmpty(request.getIngredients())) {
            errors.put("ingredients", "Ingredients is required");
        }

        // Image
        if (imagePart == null || imagePart.getSize() == 0) {
            errors.put("image", "Image is required");
        }

        if (isNullOrEmpty(request.getPrepareTime())) {
            errors.put("prepareTime", "Prepare time is required");
        }

        if (isNullOrEmpty(request.getCookingTime())) {
            errors.put("cookingTime", "Cooking time is required");
        }

        if (isNullOrEmpty(request.getYield())) {
            errors.put("yield", "Yield is required");
        }

        if (isNullOrEmpty(request.getCategory())) {
            errors.put("category", "Category is required");
        }

        if (isNullOrEmpty(request.getArea())) {
            errors.put("area", "Area is required");
        }

        if (isNullOrEmpty(request.getShortDescription())) {
            errors.put("shortDescription", "Short description is required");
        }

        if (request.getContestEntryInstructions() == null || request.getContestEntryInstructions().isEmpty()) {
            errors.put("instructions", "Instructions are required");
        } else {
            for (int i = 0; i < request.getContestEntryInstructions().size(); i++) {
                ContestEntryInstruction instruction = request.getContestEntryInstructions().get(i);

                if (instruction.getStepNumber() <= 0) {
                    errors.put("instruction[" + i + "].stepNumber", "Step number is required");
                }

                if (isNullOrEmpty(instruction.getName())) {
                    errors.put("instruction[" + i + "].name", "Name is required");
                }

                if (isNullOrEmpty(instruction.getText())) {
                    errors.put("instruction[" + i + "].text", "Instruction text is required");
                }
            }
        }

        return errors;
    }
}
