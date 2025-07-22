package com.ntn.culinary.validator;

import com.ntn.culinary.request.RecipeRequest;
import com.ntn.culinary.utils.ValidationUtils;

import javax.servlet.http.Part;
import java.util.HashMap;
import java.util.Map;

public class RecipeRequestValidator {
    public Map<String, String> validate(RecipeRequest recipeRequest, Part imagePart, boolean isUpdate) {
        Map<String, String> errors = new HashMap<>();

        if (isUpdate && ValidationUtils.isNotExistId(recipeRequest.getId())) {
            errors.put("id", "ID is required for update");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getName())) {
            errors.put("name", "Name is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getCategory())) {
            errors.put("category", "Category is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getArea())) {
            errors.put("area", "Area is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getInstructions())) {
            errors.put("instructions", "Instructions are required");
        }

        if (imagePart == null || imagePart.getSize() == 0) {
            errors.put("image", "Image is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getIngredients())) {
            errors.put("ingredients", "Ingredients are required");
        }

        if (ValidationUtils.isNotExistId(recipeRequest.getRecipedBy())) {
            errors.put("recipedBy", "Reciped by is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getPrepareTime())) {
            errors.put("prepareTime", "Prepare time is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getCookingTime())) {
            errors.put("cookingTime", "Cooking time is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getYield())) {
            errors.put("yield", "Yield is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getShortDescription())) {
            errors.put("shortDescription", "Short description is required");
        }

        if (ValidationUtils.isNullOrEmpty(recipeRequest.getAccessType())) {
            errors.put("accessType", "Access type is required");
        }

        return errors;
    }
}
