package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.*;
import com.ntn.culinary.model.Comment;
import com.ntn.culinary.model.Recipe;
import com.ntn.culinary.response.RecipeResponse;
import com.ntn.culinary.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static com.ntn.culinary.fixture.TestDataFactory.createRecipeList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecipeServiceImplTest {

    @Mock
    private RecipeDao recipeDao;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private AreaDao areaDao;

    @Mock
    private UserDao userDao;

    @Mock
    private DetailedInstructionsDao detailedInstructionsDao;

    @Mock
    private CommentDao commentDao;

    @Mock
    private NutritionDao nutritionDao;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    // Helper methods for creating test data
    private List<Recipe> createMultipleRecipeList() {
        Recipe recipe1 = createRecipe(1, "Pho Bo Bo", "Vietnamese", "Asia");
        Recipe recipe2 = createRecipe(2, "Banh Mi", "Vietnamese", "Asia");
        return List.of(recipe1, recipe2);
    }

    private Recipe createRecipe(int id, String name, String category, String area) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName(name);
        recipe.setCategory(category);
        recipe.setArea(area);
        recipe.setImage("recipe" + id + ".jpg");
        return recipe;
    }

    private Comment createComment(int id, String content, int recipeId) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setContent(content);
        comment.setRecipeId(recipeId);
        return comment;
    }

    // TEST SEARCH AND FILTER FREE RECIPES - COMPREHENSIVE CASES
    @Test
    @DisplayName("Test search and filter free recipes with empty result")
    void testSearchAndFilterFreeRecipes_withEmptyResult_shouldReturnEmptyList() {
        // Arrange
        when(recipeDao.searchAndFilterFreeRecipes("abc", null, null, 0, "FREE", 0, 10))
                .thenReturn(Collections.emptyList());
        when(commentDao.getCommentsByRecipeIds(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());
        when(nutritionDao.getNutritionByRecipeIds(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                "abc", null, null, 0, "FREE", 0, 10
        );

        // Assert
        assertTrue(responses.isEmpty());
        verify(recipeDao).searchAndFilterFreeRecipes("abc", null, null, 0, "FREE", 0, 10);
    }

    @Test
    @DisplayName("Test search and filter free recipes with null parameters")
    void testSearchAndFilterFreeRecipes_withNullParameters_shouldHandleGracefully() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        when(recipeDao.searchAndFilterFreeRecipes(null, null, null, 0, "FREE", 0, 10))
                .thenReturn(recipes);
        when(commentDao.getCommentsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(nutritionDao.getNutritionByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                null, null, null, 0, "FREE", 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
        verify(recipeDao).searchAndFilterFreeRecipes(null, null, null, 0, "FREE", 0, 10);
    }

    @Test
    @DisplayName("Test search and filter free recipes with keyword only")
    void testSearchAndFilterFreeRecipes_withKeywordOnly_shouldReturnFilteredResults() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        when(recipeDao.searchAndFilterFreeRecipes("chicken", null, null, 0, null, 0, 10))
                .thenReturn(recipes);
        when(commentDao.getCommentsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(nutritionDao.getNutritionByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                "chicken", null, null, 0, null, 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
        verify(recipeDao).searchAndFilterFreeRecipes("chicken", null, null, 0, null, 0, 10);
    }

    @Test
    @DisplayName("Test search and filter free recipes with category filter")
    void testSearchAndFilterFreeRecipes_withCategoryFilter_shouldCapitalizeCategory() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        when(recipeDao.searchAndFilterFreeRecipes(null, "Vietnamese", null, 0, null, 0, 10))
                .thenReturn(recipes);
        when(commentDao.getCommentsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(nutritionDao.getNutritionByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                null, "vietnamese", null, 0, null, 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
        verify(recipeDao).searchAndFilterFreeRecipes(null, "Vietnamese", null, 0, null, 0, 10);
    }

    @Test
    @DisplayName("Test search and filter free recipes with blank category and area")
    void testSearchAndFilterFreeRecipes_withBlankCategoryAndArea_shouldConvertToNull() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        when(recipeDao.searchAndFilterFreeRecipes(null, null, null, 0, null, 0, 10))
                .thenReturn(recipes);
        when(commentDao.getCommentsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(nutritionDao.getNutritionByRecipeIds(anyList())).thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList())).thenReturn(Collections.emptyMap());

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                null, "  ", "  ", 0, null, 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
        verify(recipeDao).searchAndFilterFreeRecipes(null, null, null, 0, null, 0, 10);
    }

    @Test
    @DisplayName("Test count search and filter free recipes with null access type")
    void testCountSearchAndFilterFreeRecipes_withNullAccessType_shouldHandleGracefully() {
        // Arrange
        when(recipeDao.countSearchAndFilterFreeRecipes(null, null, null, 0, null))
                .thenReturn(10);

        // Act
        int count = recipeService.countSearchAndFilterFreeRecipes(
                null, null, null, 0, null
        );

        // Assert
        assertEquals(10, count);
        verify(recipeDao).countSearchAndFilterFreeRecipes(null, null, null, 0, null);
    }

    @Test
    @DisplayName("Test search and filter recipes with null parameters")
    void testSearchAndFilterRecipes_withNullParameters_shouldHandleGracefully() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        when(recipeDao.searchAndFilterRecipes("keyword", null, null, 0, 0, 10))
                .thenReturn(recipes);

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterRecipes(
                "keyword", null, null, 0, 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
        verify(recipeDao).searchAndFilterRecipes("keyword", null, null, 0, 0, 10);
    }

    @Test
    @DisplayName("Test count search and filter recipes with null parameters")
    void testCountSearchAndFilterRecipes_withNullParameters_shouldHandleGracefully() {
        // Arrange
        when(recipeDao.countSearchAndFilterRecipes(null, null, null, 0))
                .thenReturn(15);

        // Act
        int count = recipeService.countSearchAndFilterRecipes(null, null, null, 0);

        // Assert
        assertEquals(15, count);
        verify(recipeDao).countSearchAndFilterRecipes(null, null, null, 0);
    }
}
