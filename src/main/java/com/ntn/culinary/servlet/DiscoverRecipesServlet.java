package com.ntn.culinary.servlet;

import com.ntn.culinary.dao.*;
import com.ntn.culinary.dao.impl.*;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.RecipePageResponse;
import com.ntn.culinary.response.RecipeResponse;
import com.ntn.culinary.service.RecipeService;
import com.ntn.culinary.service.impl.RecipeServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/discover/recipes")
public class DiscoverRecipesServlet extends HttpServlet {
    private final RecipeService recipeService;

    public DiscoverRecipesServlet() {
        RecipeDao recipeDao = new RecipeDaoImpl();
        CategoryDao categoryDao = new CategoryDaoImpl();
        AreaDao areaDao = new AreaDaoImpl();
        UserDao userDao = new UserDaoImpl();
        DetailedInstructionsDao detailedInstructionsDao = new DetailedInstructionsDaoImpl();
        CommentDao commentDao = new CommentDaoImpl();
        NutritionDao nutritionDao = new NutritionDaoImpl();

        this.recipeService = new RecipeServiceImpl(recipeDao, categoryDao, areaDao, userDao, detailedInstructionsDao, commentDao, nutritionDao);
    }

    // Thêm nhiều các bộ lọc và tìm kiếm cho các công thức nấu ăn

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Get parameters from the request
            String pageParam = req.getParameter("page");
            String sizeParam = req.getParameter("size");
            String keyword = req.getParameter("keyword");
            String category = req.getParameter("category");
            String area = req.getParameter("area");

            int recipedBy = 0;
            if (req.getParameter("recipedBy") != null) {
                recipedBy = Integer.parseInt(req.getParameter("recipedBy"));
            }

            int page = parseOrDefault(pageParam, 1);
            int size = parseOrDefault(sizeParam, 10);

            String accessType = "FREE"; // Default access type

            List<RecipeResponse> recipes = recipeService.searchAndFilterFreeRecipes(keyword, category, area, recipedBy, accessType, page, size);
            int totalItems = recipeService.countSearchAndFilterFreeRecipes(keyword, category, area, recipedBy, accessType);
            int totalPages = (int) Math.ceil((double) totalItems / size);

            RecipePageResponse response = new RecipePageResponse(recipes, totalItems, page, totalPages);
            sendResponse(resp, success(200, "Free recipes fetched successfully", response));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid page or size parameter"));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    private int parseOrDefault(String param, int defaultValue) {
        return param != null ? Integer.parseInt(param) : defaultValue;
    }
}
