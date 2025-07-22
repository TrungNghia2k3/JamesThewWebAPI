package com.ntn.culinary.service.impl;

import com.ntn.culinary.constant.AccessType;
import com.ntn.culinary.dao.*;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.Comment;
import com.ntn.culinary.model.DetailedInstructions;
import com.ntn.culinary.model.Nutrition;
import com.ntn.culinary.model.Recipe;
import com.ntn.culinary.request.RecipeRequest;
import com.ntn.culinary.response.RecipeResponse;
import com.ntn.culinary.service.RecipeService;
import com.ntn.culinary.utils.ImageUtils;

import javax.servlet.http.Part;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ntn.culinary.utils.StringUtils.capitalize;

public class RecipeServiceImpl implements RecipeService {

    private final String baseUrl = "http://localhost:8080/JamesThewWebApplication";

    private final RecipeDao recipeDao;
    private final CategoryDao categoryDao;
    private final AreaDao areaDao;
    private final UserDao userDao;
    private final DetailedInstructionsDao detailedInstructionsDao;
    private final CommentDao commentDao;
    private final NutritionDao nutritionDao;

    public RecipeServiceImpl(RecipeDao recipeDao, CategoryDao categoryDao, AreaDao areaDao, UserDao userDao, DetailedInstructionsDao detailedInstructionsDao, CommentDao commentDao, NutritionDao nutritionDao) {
        this.recipeDao = recipeDao;
        this.categoryDao = categoryDao;
        this.areaDao = areaDao;
        this.userDao = userDao;
        this.detailedInstructionsDao = detailedInstructionsDao;
        this.commentDao = commentDao;
        this.nutritionDao = nutritionDao;
    }

    @Override
    public void addRecipe(RecipeRequest recipeRequest, Part imagePart) {
        validateRecipeRequest(recipeRequest);

        if (imagePart != null && imagePart.getSize() > 0) {
            String slug = ImageUtils.slugify(recipeRequest.getName());
            String fileName = ImageUtils.saveImage(imagePart, slug, "recipes");
            recipeRequest.setImage(fileName);
        }

        recipeDao.addRecipe(mapRequestToRecipe(recipeRequest));
    }

    @Override
    public void updateRecipe(RecipeRequest recipeRequest, Part imagePart) {
        validateRecipeRequest(recipeRequest);

        Recipe existingRecipe = recipeDao.getRecipeById(recipeRequest.getId());
        if (existingRecipe == null) {
            throw new NotFoundException("Recipe with ID " + recipeRequest.getId() + " does not exist.");
        }

        if (imagePart != null && imagePart.getSize() > 0) {
            // Delete old image if it exists
            if (existingRecipe.getImage() != null) {
                ImageUtils.deleteImage(existingRecipe.getImage(), "recipes");
            }
            String slug = ImageUtils.slugify(recipeRequest.getName());
            String fileName = ImageUtils.saveImage(imagePart, slug, "recipes");
            recipeRequest.setImage(fileName);
        } else {
            recipeRequest.setImage(existingRecipe.getImage());
        }

        recipeDao.updateRecipe(mapRequestToRecipe(recipeRequest));
    }

    @Override
    public void deleteRecipe(int id) {
        Recipe existingRecipe = recipeDao.getRecipeById(id);
        if (existingRecipe == null) {
            throw new NotFoundException("Recipe with ID " + id + " does not exist.");
        }

        // Delete image if it exists
        if (existingRecipe.getImage() != null) {
            ImageUtils.deleteImage(existingRecipe.getImage(), "recipes");
        }

        recipeDao.deleteRecipe(id);
    }

    @Override
    public RecipeResponse getFreeRecipeById(int id) {
        Recipe recipe = recipeDao.getFreeRecipeById(id);
        if (recipe == null) {
            throw new NotFoundException("Recipe not found");
        }
        return mapRecipeToResponse(recipe);
    }

    @Override
    public RecipeResponse getRecipeById(int id) {
        if (recipeDao.existsById(id)) {
            Recipe recipe = recipeDao.getRecipeById(id);
            return mapRecipeToResponse(recipe);
        } else {
            throw new NotFoundException("Recipe with ID " + id + " does not exist.");
        }
    }

    @Override
    public List<RecipeResponse> getAllFreeRecipes(int page, int size) {

        List<Recipe> recipes = recipeDao.getAllFreeRecipes(page, size);
        List<Integer> recipeIds = recipes.stream().map(Recipe::getId).toList();

        Map<Integer, List<Comment>> commentsMap = commentDao.getCommentsByRecipeIds(recipeIds);
        Map<Integer, Nutrition> nutritionMap = nutritionDao.getNutritionByRecipeIds(recipeIds);
        Map<Integer, List<DetailedInstructions>> instructionsMap = detailedInstructionsDao.getDetailedInstructionsByRecipeIds(recipeIds);

        return recipes.stream()
                .map(recipe -> mapRecipeToResponse(recipe, commentsMap, nutritionMap, instructionsMap))
                .toList();

    }

    @Override
    public List<RecipeResponse> getAllRecipes(int page, int size) {
        return recipeDao.getAllRecipes(page, size).stream()
                .map(this::mapRecipeToResponse)
                .toList();
    }

    @Override
    public List<RecipeResponse> searchAndFilterFreeRecipes(String keyword, String category, String area, int createdBy, String accessType, int page, int size) {

        if (category != null && !category.isBlank()) {
            category = capitalize(category);
        }

        if (area != null && !area.isBlank()) {
            area = capitalize(area);
        }

        List<Recipe> recipes = recipeDao.searchAndFilterFreeRecipes(keyword, category, area, createdBy, accessType, page, size);
        List<Integer> recipeIds = recipes.stream().map(Recipe::getId).toList();

        Map<Integer, List<Comment>> commentsMap = commentDao.getCommentsByRecipeIds(recipeIds);
        Map<Integer, Nutrition> nutritionMap = nutritionDao.getNutritionByRecipeIds(recipeIds);
        Map<Integer, List<DetailedInstructions>> instructionsMap = detailedInstructionsDao.getDetailedInstructionsByRecipeIds(recipeIds);


        return recipes.stream()
                .map(recipe -> mapRecipeToResponse(recipe, commentsMap, nutritionMap, instructionsMap))
                .toList();
    }

    @Override
    public List<RecipeResponse> searchAndFilterRecipes(String keyword, String category, String area, int recipedBy, int page, int size) {

        if (category != null) {
            category = capitalize(category);
        }

        if (area != null) {
            area = capitalize(area);
        }

        return recipeDao.searchAndFilterRecipes(keyword, category, area, recipedBy, page, size).stream()
                .map(this::mapRecipeToResponse)
                .toList();
    }

    @Override
    public List<RecipeResponse> getAllRecipesByUserId(int userId, int page, int size) {
        return recipeDao.getAllRecipesByUserId(userId, page, size).stream()
                .map(this::mapRecipeToResponse)
                .toList();
    }

    @Override
    public List<RecipeResponse> getAllFreeRecipesByCategory(String category, int page, int size) {
        return recipeDao.getAllFreeRecipesByCategory(category, page, size).stream()
                .map(this::mapRecipeToResponse)
                .toList();
    }

    @Override
    public List<RecipeResponse> getAllRecipesByCategory(String category, int page, int size) {
        return recipeDao.getAllRecipesByCategory(category, page, size).stream()
                .map(this::mapRecipeToResponse)
                .toList();
    }

    @Override
    public List<RecipeResponse> getAllFreeRecipesByArea(String area, int page, int size) {
        return recipeDao.getAllFreeRecipesByArea(area, page, size).stream()
                .map(this::mapRecipeToResponse)
                .toList();
    }

    @Override
    public List<RecipeResponse> getAllRecipesByArea(String area, int page, int size) {
        return recipeDao.getAllRecipesByArea(area, page, size).stream()
                .map(this::mapRecipeToResponse)
                .toList();
    }

    @Override
    public int countSearchAndFilterFreeRecipes(String keyword, String category, String area, int recipedBy, String accessType) {

        if (category != null) {
            category = capitalize(category);
        }

        if (area != null) {
            area = capitalize(area);
        }

        return recipeDao.countSearchAndFilterFreeRecipes(keyword, category, area, recipedBy, accessType.toUpperCase());
    }

    @Override
    public int countSearchAndFilterRecipes(String keyword, String category, String area, int recipedBy) {

        if (category != null) {
            category = capitalize(category);
        }

        if (area != null) {
            area = capitalize(area);
        }

        return recipeDao.countSearchAndFilterRecipes(keyword, category, area, recipedBy);
    }

    @Override
    public int countAllFreeRecipes() {
        return recipeDao.countAllFreeRecipes();
    }

    @Override
    public int countAllRecipes() {
        return recipeDao.countAllRecipes();
    }

    @Override
    public int countAllRecipesByUserId(int userId) {
        return recipeDao.countAllRecipesByUserId(userId);
    }

    @Override
    public int countAllFreeRecipesByCategory(String category) {
        return recipeDao.countAllFreeRecipesByCategory(category);
    }

    @Override
    public int countAllRecipesByCategory(String category) {
        return recipeDao.countAllRecipesByCategory(category);
    }

    @Override
    public int countAllFreeRecipesByArea(String area) {
        return recipeDao.countAllFreeRecipesByArea(area);
    }

    @Override
    public int countAllRecipesByArea(String area) {
        return recipeDao.countAllRecipesByArea(area);
    }

    private void validateRecipeRequest(RecipeRequest recipeRequest) {
        if (!categoryDao.existsByName(recipeRequest.getCategory())) {
            throw new NotFoundException("Category does not exist");
        }
        if (!areaDao.existsByName(recipeRequest.getArea())) {
            throw new NotFoundException("Area does not exist");
        }
        String accessType = recipeRequest.getAccessType();
        if (!String.valueOf(AccessType.FREE).equalsIgnoreCase(accessType) &&
            !String.valueOf(AccessType.PAID).equalsIgnoreCase(accessType)) {
            throw new NotFoundException("Invalid access type");
        }
        if (!userDao.existsById(recipeRequest.getRecipedBy())) {
            throw new NotFoundException("User does not exist");
        }
    }

    private Recipe mapRequestToRecipe(RecipeRequest request) {
        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setCategory(request.getCategory());
        recipe.setArea(request.getArea());
        recipe.setInstructions(request.getInstructions());
        recipe.setImage(request.getImage());
        recipe.setIngredients(request.getIngredients());
        recipe.setPublishedOn(new Date(System.currentTimeMillis()));
        recipe.setRecipedBy(request.getRecipedBy());
        recipe.setPrepareTime(request.getPrepareTime());
        recipe.setCookingTime(request.getCookingTime());
        recipe.setYield(request.getYield());
        recipe.setShortDescription(request.getShortDescription());
        recipe.setAccessType(request.getAccessType());
        return recipe;
    }

    private RecipeResponse mapRecipeToResponse(Recipe recipe) {
        String imageUrl = "http://localhost:8080/JamesThewWebApplication/api/images/recipes/" + recipe.getImage();

        String detailedInstructionImageUrl = "http://localhost:8080/JamesThewWebApplication/api/images/instructions/";

        // Add image URL to each detailed instruction
        List<DetailedInstructions> updatedDetailedInstructions = Optional
                .ofNullable(detailedInstructionsDao.getDetailedInstructionsByRecipeId(recipe.getId()))
                .orElse(Collections.emptyList())
                .stream()
                .peek(instruction -> {
                    if (instruction.getImage() != null) {
                        instruction.setImage(detailedInstructionImageUrl + instruction.getImage());
                    }
                })
                .toList();

        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getCategory(),
                recipe.getArea(),
                recipe.getInstructions(),
                imageUrl,
                recipe.getIngredients(),
                recipe.getPublishedOn(),
                recipe.getRecipedBy(),
                recipe.getPrepareTime(),
                recipe.getCookingTime(),
                recipe.getYield(),
                recipe.getShortDescription(),
                recipe.getAccessType(),
                commentDao.getCommentsByRecipeId(recipe.getId()),
                nutritionDao.getNutritionByRecipeId(recipe.getId()),
                updatedDetailedInstructions
        );
    }

    private RecipeResponse mapRecipeToResponse(
            Recipe recipe, Map<Integer, List<Comment>> commentsMap, Map<Integer, Nutrition> nutritionMap, Map<Integer, List<DetailedInstructions>> instructionsMap) {

        String imageUrl = baseUrl + "/api/images/recipes/" + recipe.getImage();
        String instructionImageUrl = baseUrl + "/api/images/instructions/";

        List<DetailedInstructions> instructions = Optional.ofNullable(instructionsMap.get(recipe.getId()))
                .orElse(Collections.emptyList())
                .stream()
                .peek(ins -> {
                    if (ins.getImage() != null) {
                        ins.setImage(instructionImageUrl + ins.getImage());
                    }
                })
                .toList();

        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getCategory(),
                recipe.getArea(),
                recipe.getInstructions(),
                imageUrl,
                recipe.getIngredients(),
                recipe.getPublishedOn(),
                recipe.getRecipedBy(),
                recipe.getPrepareTime(),
                recipe.getCookingTime(),
                recipe.getYield(),
                recipe.getShortDescription(),
                recipe.getAccessType(),
                commentsMap.getOrDefault(recipe.getId(), List.of()),
                nutritionMap.getOrDefault(recipe.getId(), new Nutrition()),
                instructions
        );
    }


}