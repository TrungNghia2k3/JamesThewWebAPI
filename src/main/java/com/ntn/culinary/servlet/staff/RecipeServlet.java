package com.ntn.culinary.servlet.staff;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.*;
import com.ntn.culinary.dao.impl.*;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.request.RecipeRequest;
import com.ntn.culinary.response.RecipePageResponse;
import com.ntn.culinary.response.RecipeResponse;
import com.ntn.culinary.service.RecipeService;
import com.ntn.culinary.service.impl.RecipeServiceImpl;
import com.ntn.culinary.validator.RecipeRequestValidator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/staff/recipes")
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
            String userIdParam = req.getParameter("userId");

            // Parse pagination parameters
            int page = parseOrDefault(pageParam, 1);
            int size = parseOrDefault(sizeParam, 10);

            // Validate pagination parameters
            if (page < 1 || size < 1) {
                throw new IllegalArgumentException("Page and Size cannot be less than 1");
            }

            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                RecipeResponse recipe = recipeService.getRecipeById(id);
                sendResponse(resp, success(200, "Recipe found", recipe));
            } else if (areaParam != null) {
                // Fetch recipes by area with pagination
                List<RecipeResponse> recipes = recipeService.getAllRecipesByArea(areaParam, page, size);
                int totalItems = recipeService.countAllRecipesByArea(areaParam);
                int totalPages = (int) Math.ceil((double) totalItems / size);
                RecipePageResponse response = new RecipePageResponse(recipes, totalItems, page, totalPages);
                sendResponse(resp, success(200, "Recipes by area fetched successfully", response));
            } else if (categoryParam != null) {
                // Fetch recipes by category with pagination
                List<RecipeResponse> recipes = recipeService.getAllRecipesByCategory(categoryParam, page, size);
                int totalItems = recipeService.countAllRecipesByCategory(categoryParam);
                int totalPages = (int) Math.ceil((double) totalItems / size);
                RecipePageResponse response = new RecipePageResponse(recipes, totalItems, page, totalPages);
                sendResponse(resp,success(200, "Recipes by category fetched successfully", response));
            } else if (userIdParam != null) {
                // Fetch recipes by user with pagination
                int userId = Integer.parseInt(userIdParam);
                List<RecipeResponse> recipes = recipeService.getAllRecipesByUserId(userId, page, size);
                int totalItems = recipeService.countAllRecipesByUserId(userId);
                int totalPages = (int) Math.ceil((double) totalItems / size);
                RecipePageResponse response = new RecipePageResponse(recipes, totalItems, page, totalPages);
                sendResponse(resp, success(200, "Recipes by user fetched successfully", response));
            } else {
                // Fetch recipes with pagination
                List<RecipeResponse> recipes = recipeService.getAllRecipes(page, size);
                int totalItems = recipeService.countAllRecipes();
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");

            // Check if request is multipart/form-data
            String contentType = req.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")) {
                sendResponse(resp, error(400, "Content-Type must be multipart/form-data"));
                return;
            }

            // Use Apache Commons FileUpload
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");
            List<FileItem> items = upload.parseRequest(req);

            Map<String, String> formFields = new java.util.HashMap<>();
            Part imagePart = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    formFields.put(item.getFieldName(), item.getString("UTF-8"));
                } else if ("image".equals(item.getFieldName())) {
                    if (item.getSize() > 0 && item.getName() != null && !item.getName().isEmpty()) {
                        imagePart = new com.ntn.culinary.helper.FileItemPart(item);
                    }
                }
            }

            // Required fields
            String[] requiredFields = {
                    "name", "category", "area", "instructions", "ingredients", "publishedOn",
                    "prepareTime", "cookingTime", "yield", "shortDescription", "accessType", "recipedBy"
            };
            for (String field : requiredFields) {
                if (!formFields.containsKey(field) || formFields.get(field) == null || formFields.get(field).trim().isEmpty()) {
                    sendResponse(resp, error(400, "Missing or empty field: " + field));
                    return;
                }
            }
            if (imagePart == null) {
                sendResponse(resp, error(400, "Missing or empty image file. Please upload an image."));
                return;
            }

            // Parse fields
            String name = formFields.get("name");
            String category = formFields.get("category");
            String area = formFields.get("area");
            String instructions = formFields.get("instructions");
            String ingredients = formFields.get("ingredients");
            String publishedOn = formFields.get("publishedOn");
            String prepareTime = formFields.get("prepareTime");
            String cookingTime = formFields.get("cookingTime");
            String yield = formFields.get("yield");
            String shortDescription = formFields.get("shortDescription");
            String accessType = formFields.get("accessType");
            int recipedBy = Integer.parseInt(formFields.get("recipedBy"));

            RecipeRequest recipeRequest = new RecipeRequest();
            recipeRequest.setName(name);
            recipeRequest.setCategory(category);
            recipeRequest.setArea(area);
            recipeRequest.setInstructions(instructions);
            recipeRequest.setIngredients(ingredients);
            recipeRequest.setPublishedOn(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(publishedOn));
            recipeRequest.setRecipedBy(recipedBy);
            recipeRequest.setPrepareTime(prepareTime);
            recipeRequest.setCookingTime(cookingTime);
            recipeRequest.setYield(yield);
            recipeRequest.setShortDescription(shortDescription);
            recipeRequest.setAccessType(accessType);

            // Validate input
            RecipeRequestValidator validator = new RecipeRequestValidator();
            Map<String, String> errors = validator.validate(recipeRequest, imagePart, false);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            recipeService.addRecipe(recipeRequest, imagePart);
            sendResponse(resp, success(200, "Recipe added successfully"));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload: " + e.getMessage()));
        } catch (org.apache.commons.fileupload.FileUploadException e) {
            sendResponse(resp, error(400, "File upload error: " + e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");

            // Check if request is multipart/form-data
            String contentType = req.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")) {
                sendResponse(resp, error(400, "Content-Type must be multipart/form-data"));
                return;
            }

            // Use Apache Commons FileUpload
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");
            List<FileItem> items = upload.parseRequest(req);

            Map<String, String> formFields = new java.util.HashMap<>();
            Part imagePart = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    formFields.put(item.getFieldName(), item.getString("UTF-8"));
                } else if ("image".equals(item.getFieldName())) {
                    if (item.getSize() > 0 && item.getName() != null && !item.getName().isEmpty()) {
                        imagePart = new com.ntn.culinary.helper.FileItemPart(item);
                    }
                }
            }

            // Required fields for update (id is required)
            String[] requiredFields = {
                    "id", "name", "category", "area", "instructions", "ingredients", "publishedOn",
                    "prepareTime", "cookingTime", "yield", "shortDescription", "accessType", "recipedBy"
            };
            for (String field : requiredFields) {
                if (!formFields.containsKey(field) || formFields.get(field) == null || formFields.get(field).trim().isEmpty()) {
                    sendResponse(resp, error(400, "Missing or empty field: " + field));
                    return;
                }
            }
            // imagePart is optional for update

            // Parse fields
            int id = Integer.parseInt(formFields.get("id"));
            String name = formFields.get("name");
            String category = formFields.get("category");
            String area = formFields.get("area");
            String instructions = formFields.get("instructions");
            String ingredients = formFields.get("ingredients");
            String publishedOn = formFields.get("publishedOn");
            String prepareTime = formFields.get("prepareTime");
            String cookingTime = formFields.get("cookingTime");
            String yield = formFields.get("yield");
            String shortDescription = formFields.get("shortDescription");
            String accessType = formFields.get("accessType");
            int recipedBy = Integer.parseInt(formFields.get("recipedBy"));

            RecipeRequest recipeRequest = new RecipeRequest();
            recipeRequest.setId(id);
            recipeRequest.setName(name);
            recipeRequest.setCategory(category);
            recipeRequest.setArea(area);
            recipeRequest.setInstructions(instructions);
            recipeRequest.setIngredients(ingredients);
            recipeRequest.setPublishedOn(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(publishedOn));
            recipeRequest.setRecipedBy(recipedBy);
            recipeRequest.setPrepareTime(prepareTime);
            recipeRequest.setCookingTime(cookingTime);
            recipeRequest.setYield(yield);
            recipeRequest.setShortDescription(shortDescription);
            recipeRequest.setAccessType(accessType);

            // Validate input
            RecipeRequestValidator validator = new RecipeRequestValidator();
            Map<String, String> errors = validator.validate(recipeRequest, imagePart, true);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            recipeService.updateRecipe(recipeRequest, imagePart);
            sendResponse(resp, success(200, "Recipe updated successfully"));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload: " + e.getMessage()));
        } catch (org.apache.commons.fileupload.FileUploadException e) {
            sendResponse(resp, error(400, "File upload error: " + e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("Recipe ID is required");
            }

            int recipeId = Integer.parseInt(idParam);
            recipeService.deleteRecipe(recipeId);
            sendResponse(resp, success(200, "Recipe deleted successfully"));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid recipe ID format"));
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
