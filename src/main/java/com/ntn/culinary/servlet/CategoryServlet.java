package com.ntn.culinary.servlet;

import com.ntn.culinary.dao.CategoryDao;
import com.ntn.culinary.dao.impl.CategoryDaoImpl;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.CategoryResponse;
import com.ntn.culinary.service.CategoryService;
import com.ntn.culinary.service.ImageService;
import com.ntn.culinary.service.impl.CategoryServiceImpl;
import com.ntn.culinary.service.impl.ImageServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/categories")
public class CategoryServlet extends HttpServlet {

    private final CategoryService categoryService;

    public CategoryServlet() {
        CategoryDao categoryDao = new CategoryDaoImpl();
        ImageService imageService = new ImageServiceImpl();
        this.categoryService = new CategoryServiceImpl(categoryDao, imageService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Lấy thông tin từ request
            String idParam = req.getParameter("id");
            String nameParam = req.getParameter("name");

            // Get category by ID or name
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                CategoryResponse category = categoryService.getCategoryById(id);
                sendResponse(resp, success(200, "Category fetched successfully", category));
            } else if (nameParam != null) {
                CategoryResponse category = categoryService.getCategoryByName(nameParam);
                if (category == null) {
                    throw new NotFoundException("Category not found with name: " + nameParam);
                }
                sendResponse(resp, success(200, "Category fetched successfully", category));
            } else {
                // If no ID or name provided, return all categories
                List<CategoryResponse> categories = categoryService.getAllCategories();
                sendResponse(resp, success(200, "All categories fetched successfully", categories));
            }
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid ID format"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
