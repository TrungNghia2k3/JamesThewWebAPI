package com.ntn.culinary.service;

import com.ntn.culinary.request.RecipeRequest;
import com.ntn.culinary.response.RecipeResponse;

import javax.servlet.http.Part;
import java.util.List;

public interface RecipeService {
    /**
         * Adds a new recipe with the provided details and image.
         *
         * @param recipeRequest the recipe details
         * @param imagePart the image file part
         */
        void addRecipe(RecipeRequest recipeRequest, Part imagePart);

        /**
         * Updates an existing recipe with the provided details and image.
         *
         * @param recipeRequest the updated recipe details
         * @param imagePart the new image file part
         */
        void updateRecipe(RecipeRequest recipeRequest, Part imagePart);

        /**
         * Deletes a recipe by its ID.
         *
         * @param id the ID of the recipe to delete
         */
        void deleteRecipe(int id);

        /**
         * Retrieves a free recipe by its ID.
         *
         * @param id the ID of the recipe
         * @return the recipe response
         */
        RecipeResponse getFreeRecipeById(int id);

        /**
         * Retrieves a recipe by its ID.
         *
         * @param id the ID of the recipe
         * @return the recipe response
         */
        RecipeResponse getRecipeById(int id);

        /**
         * Retrieves a paginated list of all free recipes.
         *
         * @param page the page number
         * @param size the page size
         * @return list of free recipe responses
         */
        List<RecipeResponse> getAllFreeRecipes(int page, int size);

        /**
         * Retrieves a paginated list of all recipes.
         *
         * @param page the page number
         * @param size the page size
         * @return list of recipe responses
         */
        List<RecipeResponse> getAllRecipes(int page, int size);

        /**
         * Searches and filters free recipes based on criteria.
         *
         * @param keyword the search keyword
         * @param category the recipe category
         * @param area the recipe area
         * @param createdBy the creator's user ID
         * @param accessType the access type
         * @param page the page number
         * @param size the page size
         * @return list of filtered free recipe responses
         */
        List<RecipeResponse> searchAndFilterFreeRecipes(String keyword, String category, String area, int createdBy, String accessType, int page, int size);

        /**
         * Searches and filters recipes based on criteria.
         *
         * @param keyword the search keyword
         * @param category the recipe category
         * @param area the recipe area
         * @param recipedBy the creator's user ID
         * @param page the page number
         * @param size the page size
         * @return list of filtered recipe responses
         */
        List<RecipeResponse> searchAndFilterRecipes(String keyword, String category, String area, int recipedBy, int page, int size);

        /**
         * Retrieves all recipes created by a specific user.
         *
         * @param userId the user ID
         * @param page the page number
         * @param size the page size
         * @return list of recipe responses
         */
        List<RecipeResponse> getAllRecipesByUserId(int userId, int page, int size);

        /**
         * Retrieves all free recipes by category.
         *
         * @param category the category
         * @param page the page number
         * @param size the page size
         * @return list of free recipe responses
         */
        List<RecipeResponse> getAllFreeRecipesByCategory(String category, int page, int size);

        /**
         * Retrieves all recipes by category.
         *
         * @param category the category
         * @param page the page number
         * @param size the page size
         * @return list of recipe responses
         */
        List<RecipeResponse> getAllRecipesByCategory(String category, int page, int size);

        /**
         * Retrieves all free recipes by area.
         *
         * @param area the area
         * @param page the page number
         * @param size the page size
         * @return list of free recipe responses
         */
        List<RecipeResponse> getAllFreeRecipesByArea(String area, int page, int size);

        /**
         * Retrieves all recipes by area ID.
         *
         * @param area the area
         * @param page the page number
         * @param size the page size
         * @return list of recipe responses
         */
        List<RecipeResponse> getAllRecipesByArea(String area, int page, int size);

        /**
         * Counts the number of free recipes matching the search and filter criteria.
         *
         * @param keyword the search keyword
         * @param category the recipe category
         * @param area the recipe area
         * @param recipedBy the creator's user ID
         * @param accessType the access type
         * @return the count of matching free recipes
         */
        int countSearchAndFilterFreeRecipes(String keyword, String category, String area, int recipedBy, String accessType);

        /**
         * Counts the number of recipes matching the search and filter criteria.
         *
         * @param keyword the search keyword
         * @param category the recipe category
         * @param area the recipe area
         * @param recipedBy the creator's user ID
         * @return the count of matching recipes
         */
        int countSearchAndFilterRecipes(String keyword, String category, String area, int recipedBy);

        /**
         * Counts all free recipes.
         *
         * @return the count of all free recipes
         */
        int countAllFreeRecipes();

        /**
         * Counts all recipes.
         *
         * @return the count of all recipes
         */
        int countAllRecipes();

        /**
         * Counts all recipes created by a specific user.
         *
         * @param userId the user ID
         * @return the count of recipes by user
         */
        int countAllRecipesByUserId(int userId);

        /**
         * Counts all free recipes by category.
         *
         * @param category the category
         * @return the count of free recipes in the category
         */
        int countAllFreeRecipesByCategory(String category);

        /**
         * Counts all recipes by category.
         *
         * @param category the category
         * @return the count of recipes in the category
         */
        int countAllRecipesByCategory(String category);

        /**
         * Counts all free recipes by area.
         *
         * @param area the area
         * @return the count of free recipes in the area
         */
        int countAllFreeRecipesByArea(String area);

        /**
         * Counts all recipes by area ID.
         *
         * @param area the area
         * @return the count of recipes in the area
         */
        int countAllRecipesByArea(String area);
}
