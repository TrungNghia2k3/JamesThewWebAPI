package com.ntn.culinary.dao;

import com.ntn.culinary.model.Nutrition;

import java.util.List;
import java.util.Map;

public interface NutritionDao {
    Nutrition getNutritionByRecipeId(int recipeId);

    void addNutrition(Nutrition nutrition);

    void updateNutrition(Nutrition nutrition);

    void deleteNutritionByNutritionIdRecipeId(int nutritionId, int recipeId);

    boolean existsByNutritionIdAndRecipeId(int nutritionId,int recipeId);

    Nutrition getNutritionByNutritionIdAndRecipeId(int nutritionId,int recipeId);

    public Map<Integer, Nutrition> getNutritionByRecipeIds(List<Integer> recipeIds);
}
