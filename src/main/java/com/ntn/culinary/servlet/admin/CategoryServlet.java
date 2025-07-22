package com.ntn.culinary.servlet.admin;

import com.ntn.culinary.dao.CategoryDao;
import com.ntn.culinary.dao.impl.CategoryDaoImpl;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.request.CategoryRequest;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.CategoryResponse;
import com.ntn.culinary.service.CategoryService;
import com.ntn.culinary.service.impl.CategoryServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/admin/categories")
public class CategoryServlet extends HttpServlet {

    private final CategoryService categoryService;

    public CategoryServlet() {
        CategoryDao categoryDao = new CategoryDaoImpl();
        this.categoryService = new CategoryServiceImpl(categoryDao);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read JSON request body
            String json = readRequestBody(req);

            // Parse JSON string to CategoryRequest object
            CategoryRequest categoryRequest = fromJson(json, CategoryRequest.class);

            // Validate request
            if (categoryRequest == null || categoryRequest.getName() == null || categoryRequest.getName().isEmpty()) {
                throw new IllegalArgumentException("Category name is required");
            }

            categoryService.addCategory(categoryRequest);
            sendResponse(resp, success(200, "Category created successfully"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read JSON request body
            String json = readRequestBody(req);

            // Parse JSON string to CategoryRequest object
            CategoryRequest categoryRequest = fromJson(json, CategoryRequest.class);

            // Validate request
            if (categoryRequest == null || categoryRequest.getId() <= 0 || categoryRequest.getName() == null || categoryRequest.getName().isEmpty()) {
                throw new IllegalArgumentException("Category ID and name are required");
            }

            categoryService.updateCategory(categoryRequest);
            sendResponse(resp, success(200, "Category updated successfully"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("Category ID is required");
            }

            int id = Integer.parseInt(idParam);
            categoryService.deleteCategory(id);
            sendResponse(resp, success(200, "Category deleted successfully"));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid category ID format"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
