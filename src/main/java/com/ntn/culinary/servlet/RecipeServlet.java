package com.ntn.culinary.servlet;

import com.ntn.culinary.dao.*;
import com.ntn.culinary.dao.impl.*;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.RecipePageResponse;
import com.ntn.culinary.response.RecipeResponse;
import com.ntn.culinary.service.RecipeService;
import com.ntn.culinary.service.impl.RecipeServiceImpl;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/recipes")
@MultipartConfig
public class RecipeServlet extends HttpServlet {
    private final RecipeService recipeService;

    public RecipeServlet() {
        RecipeDao recipeDao = new RecipeDaoImpl();
        CategoryDao categoryDao = new CategoryDaoImpl();
        AreaDao areaDao = new AreaDaoImpl();
        UserDao userDao = new UserDaoImpl();
        DetailedInstructionsDao detailedInstructionsDao = new DetailedInstructionsDaoImpl();
        CommentDao commentDao = new CommentDaoImpl();
        NutritionDao nutritionDao = new NutritionDaoImpl();
        this.recipeService = new RecipeServiceImpl(recipeDao, categoryDao, areaDao, userDao, detailedInstructionsDao, commentDao, nutritionDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Get parameters from the request
            String idParam = req.getParameter("id");
            String pageParam = req.getParameter("page");
            String sizeParam = req.getParameter("size");
            String areaParam = req.getParameter("area");
            String categoryParam = req.getParameter("category");

            // Parse pagination parameters
            int page = parseOrDefault(pageParam, 1);
            int size = parseOrDefault(sizeParam, 10);

            // Validate pagination parameters
            if (page < 1 || size < 1) {
                throw new IllegalArgumentException("Page and Size cannot be less than 1");
            }

            if (idParam != null) {
                int id = Integer.parseInt(idParam);

                RecipeResponse recipe = recipeService.getFreeRecipeById(id);
                sendResponse(resp, success(200, "Recipe found", recipe));
            } else if (areaParam != null) {
                List<RecipeResponse> recipes = recipeService.getAllFreeRecipesByArea(areaParam, page, size);
                int totalItems = recipeService.countAllFreeRecipesByArea(areaParam);
                int totalPages = (int) Math.ceil((double) totalItems / size);

                RecipePageResponse response = new RecipePageResponse(recipes, totalItems, page, totalPages);
                sendResponse(resp, success(200, "Recipes by area fetched successfully", response));
            } else if (categoryParam != null) {
                List<RecipeResponse> recipes = recipeService.getAllFreeRecipesByCategory(categoryParam, page, size);
                int totalItems = recipeService.countAllFreeRecipesByCategory(categoryParam);
                int totalPages = (int) Math.ceil((double) totalItems / size);

                RecipePageResponse response = new RecipePageResponse(recipes, totalItems, page, totalPages);
                sendResponse(resp, success(200, "Recipes by category fetched successfully", response));
            } else {
                // Fetch recipes with pagination
                List<RecipeResponse> recipes = recipeService.getAllFreeRecipes(page, size);
                int totalItems = recipeService.countAllFreeRecipes();
                int totalPages = (int) Math.ceil((double) totalItems / size);

                RecipePageResponse response = new RecipePageResponse(recipes, totalItems, page, totalPages);
                sendResponse(resp, success(200, "Free recipes fetched successfully", response));
            }
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid recipe ID"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, "Invalid pagination parameters: " + e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    private int parseOrDefault(String param, int defaultValue) {
        return param != null ? Integer.parseInt(param) : defaultValue;
    }
}
