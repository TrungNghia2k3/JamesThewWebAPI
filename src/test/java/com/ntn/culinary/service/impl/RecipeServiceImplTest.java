package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.*;
import com.ntn.culinary.model.Comment;
import com.ntn.culinary.model.DetailedInstructions;
import com.ntn.culinary.model.Nutrition;
import com.ntn.culinary.model.Recipe;
import com.ntn.culinary.response.RecipeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.fixture.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Test
    void testSearchAndFilter_withValidData_shouldReturnMappedResponse() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        List<Comment> comments = createCommentList();
        Nutrition nutrition = createNutrition();
        List<DetailedInstructions> instructions = createInstructions();

        when(recipeDao.searchAndFilterFreeRecipes(any(), any(), any(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(recipes);
        when(commentDao.getCommentsByRecipeIds(anyList()))
                .thenReturn(Map.of(1, comments));
        when(nutritionDao.getNutritionByRecipeIds(anyList()))
                .thenReturn(Map.of(1, nutrition));
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList()))
                .thenReturn(Map.of(1, instructions));

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                "pho", "vietnamese", "asia", 123, "FREE", 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
        RecipeResponse response = responses.getFirst();
        assertEquals("Pho Bo", response.getName());
        assertEquals("Vietnamese", response.getCategory());
        assertEquals(1, response.getComments().size());
        assertEquals("Great recipe!", response.getComments().getFirst().getContent());
        assertEquals(1, response.getDetailedInstructions().size());
        assertTrue(response.getImage().contains("pho.jpg"));
    }

    @Test
    void testSearchAndFilter_withEmptyResult_shouldReturnEmptyList() {
        // Arrange
        when(recipeDao.searchAndFilterFreeRecipes(any(), any(), any(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(commentDao.getCommentsByRecipeIds(anyList()))
                .thenReturn(Collections.emptyMap());
        when(nutritionDao.getNutritionByRecipeIds(anyList()))
                .thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList()))
                .thenReturn(Collections.emptyMap());

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                "abc", null, null, 0, "FREE", 0, 10
        );

        // Assert
        assertTrue(responses.isEmpty());
    }

    @Test
    void testSearchAndFilter_whenCategoryAndAreaNull_shouldNotThrow() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        when(recipeDao.searchAndFilterFreeRecipes(any(), any(), any(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(recipes);
        when(commentDao.getCommentsByRecipeIds(anyList()))
                .thenReturn(Map.of(1, Collections.emptyList()));
        when(nutritionDao.getNutritionByRecipeIds(anyList()))
                .thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList()))
                .thenReturn(Map.of(1, Collections.emptyList()));

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                null, null, null, 0, "FREE", 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
    }

    @Test
    void testSearchAndFilter_whenNullCommentsAndInstructions_shouldStillReturn() {
        // Arrange
        List<Recipe> recipes = createRecipeList();
        when(recipeDao.searchAndFilterFreeRecipes(any(), any(), any(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(recipes);
        when(commentDao.getCommentsByRecipeIds(anyList()))
                .thenReturn(Collections.emptyMap());
        when(nutritionDao.getNutritionByRecipeIds(anyList()))
                .thenReturn(Collections.emptyMap());
        when(detailedInstructionsDao.getDetailedInstructionsByRecipeIds(anyList()))
                .thenReturn(Collections.emptyMap());

        // Act
        List<RecipeResponse> responses = recipeService.searchAndFilterFreeRecipes(
                null, null, null, 0, "FREE", 0, 10
        );

        // Assert
        assertEquals(1, responses.size());
    }
}