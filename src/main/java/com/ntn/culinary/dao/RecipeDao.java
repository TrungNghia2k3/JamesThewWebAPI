package com.ntn.culinary.dao;

import com.ntn.culinary.model.Recipe;

import java.util.List;

public interface RecipeDao {
    void addRecipe(Recipe recipe);

    void updateRecipe(Recipe recipe);

    void deleteRecipe(int id);

    boolean existsById(int id);

    Recipe getRecipeById(int id);

    List<Recipe> getAllRecipes(int page, int size);

    List<Recipe> searchAndFilterRecipes(String keyword, String category, String area, int recipedBy, int page, int size);

    int countSearchAndFilterRecipes(String keyword, String category, String area, int recipedBy);

    int countAllRecipes();

    int countAllRecipesByUserId(int userId);

    int countAllFreeRecipesByCategory(String category);

    int countAllRecipesByCategory(String category);

    int countAllFreeRecipesByArea(String area);

    int countAllRecipesByArea(String area);

    List<Recipe> getAllRecipesByUserId(int userId, int page, int size);

    Recipe getFreeRecipeById(int id);

    List<Recipe> getAllFreeRecipes(int page, int size);

    List<Recipe> searchAndFilterFreeRecipes(String keyword, String category, String area, int recipedBy, String accessType, int page, int size);

    int countSearchAndFilterFreeRecipes(String keyword, String category, String area, int recipedBy, String accessType);

    int countAllFreeRecipes();

    List<Recipe> getAllFreeRecipesByCategory(String category, int page, int size);

    List<Recipe> getAllRecipesByCategory(String category, int page, int size);

    List<Recipe> getAllFreeRecipesByArea(String area, int page, int size);

    List<Recipe> getAllRecipesByArea(String area, int page, int size);


}
