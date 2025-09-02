package com.ntn.culinary.servlet.general;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ntn.culinary.dao.*;
import com.ntn.culinary.dao.impl.*;
import com.ntn.culinary.exception.BadRequestException;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.helper.FileItemPart;
import com.ntn.culinary.model.ContestEntryInstruction;
import com.ntn.culinary.request.ContestEntryRequest;
import com.ntn.culinary.request.DeleteContestEntryRequest;
import com.ntn.culinary.response.ContestEntryResponse;
import com.ntn.culinary.service.ContestEntryService;
import com.ntn.culinary.service.ImageService;
import com.ntn.culinary.service.impl.ContestEntryServiceImpl;
import com.ntn.culinary.service.impl.ImageServiceImpl;
import com.ntn.culinary.utils.GsonUtils;
import com.ntn.culinary.validator.ContestEntryRequestValidator;
import com.ntn.culinary.validator.DeleteContestEntryRequestValidator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/general/contest-entry")
@MultipartConfig
public class ContestEntryServlet extends HttpServlet {
    private final ContestEntryService contestEntryService;

    public ContestEntryServlet() {
        ContestEntryDao contestEntryDao = new ContestEntryDaoImpl();
        ContestEntryInstructionsDao contestEntryInstructionsDao = new ContestEntryInstructionsDaoImpl();
        UserDao userDao = new UserDaoImpl();
        CategoryDao categoryDao = new CategoryDaoImpl();
        AreaDao areaDao = new AreaDaoImpl();
        ContestDao contestDao = new ContestDaoImpl();
        ImageService imageService = new ImageServiceImpl();

        this.contestEntryService = new ContestEntryServiceImpl(contestEntryDao, contestEntryInstructionsDao, userDao, categoryDao, areaDao, contestDao, imageService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String idParam = req.getParameter("id");
            String contestIdParam = req.getParameter("contestId");
            String userIdParam = req.getParameter("userId");

            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                ContestEntryResponse contestEntry = contestEntryService.getContestEntryById(id);
                sendResponse(resp, success(200, "Contest entry fetched successfully", contestEntry));
            } else if (contestIdParam != null && userIdParam != null) {
                int contestId = Integer.parseInt(contestIdParam);
                int userId = Integer.parseInt(userIdParam);
                ContestEntryResponse contestEntry = contestEntryService.getContestEntryByUserIdAndContestId(userId, contestId);
                sendResponse(resp, success(200, "Contest entry fetched successfully", contestEntry));
            } else if (userIdParam != null) {
                int userId = Integer.parseInt(userIdParam);
                List<ContestEntryResponse> userContestEntries = contestEntryService.getContestEntriesByUserId(userId);
                sendResponse(resp, success(200, "User's contest entries fetched successfully", userContestEntries));
            }
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid ID format"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");

            // Check if request is multipart/form-data
            if (!ServletFileUpload.isMultipartContent(req)) {
                throw new BadRequestException("Invalid request type. Expected multipart/form-data.");
            }

            // Create factory and upload handler
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");

            // Parse request
            List<FileItem> items = upload.parseRequest(req);

            // Store form fields and file
            Map<String, String> formFields = new HashMap<>();
            Part imagePart = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");
                    formFields.put(fieldName, fieldValue);
                } else if ("image".equals(item.getFieldName())) {
                    if (item.getSize() > 0 && item.getName() != null && !item.getName().isEmpty()) {
                        imagePart = new FileItemPart(item);
                    }
                }
            }

            // Required fields
            String[] requiredFields = {
                    "contestId", "userId", "name", "ingredients", "prepareTime",
                    "cookingTime", "yield", "category", "area", "shortDescription", "contestEntryInstructions"
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
            int contestId = Integer.parseInt(formFields.get("contestId"));
            int userId = Integer.parseInt(formFields.get("userId"));
            String name = formFields.get("name");
            String ingredients = formFields.get("ingredients");
            String prepareTime = formFields.get("prepareTime");
            String cookingTime = formFields.get("cookingTime");
            String yield = formFields.get("yield");
            String category = formFields.get("category");
            String area = formFields.get("area");
            String shortDescription = formFields.get("shortDescription");
            String json = formFields.get("contestEntryInstructions");

            // Debug logging
            System.out.println("=== DEBUG VALUES ===");
            System.out.println("contestId: " + contestId);
            System.out.println("userId: " + userId);
            System.out.println("name: " + name);
            System.out.println("image present: " + (imagePart != null));

            // Parse JSON instructions
            Type listType = new TypeToken<List<ContestEntryInstruction>>() {}.getType();
            List<ContestEntryInstruction> instructions = new ArrayList<>();
            if (json != null && !json.trim().isEmpty()) {
                instructions = GsonUtils.getGson().fromJson(json, listType);
            }

            // Set values to ContestEntryRequest
            ContestEntryRequest contestEntryRequest = new ContestEntryRequest();
            contestEntryRequest.setContestId(contestId);
            contestEntryRequest.setUserId(userId);
            contestEntryRequest.setName(name);
            contestEntryRequest.setIngredients(ingredients);
            contestEntryRequest.setPrepareTime(prepareTime);
            contestEntryRequest.setCookingTime(cookingTime);
            contestEntryRequest.setYield(yield);
            contestEntryRequest.setCategory(category);
            contestEntryRequest.setArea(area);
            contestEntryRequest.setShortDescription(shortDescription);
            contestEntryRequest.setContestEntryInstructions(instructions);

            // Validate input
            ContestEntryRequestValidator validator = new ContestEntryRequestValidator();
            Map<String, String> errors = validator.validateContestEntryRequest(contestEntryRequest, imagePart, false);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            contestEntryService.addContestEntry(contestEntryRequest, imagePart);
            sendResponse(resp, success(201, "Contest entry created successfully", null));

        } catch (BadRequestException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload: " + e.getMessage()));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data: " + e.getMessage()));
        } catch (FileUploadException e) {
            sendResponse(resp, error(400, "File upload error: " + e.getMessage()));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid number format: " + e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
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

            // Manual check for multipart/form-data (Commons FileUpload only supports POST by default)
            String contentType = req.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")) {
                throw new BadRequestException("Invalid request type. Expected multipart/form-data. Actual Content-Type: " + contentType);
            }

            // Create factory and upload handler
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");

            // Parse request
            List<FileItem> items = upload.parseRequest(req);

            // Store form fields and file
            Map<String, String> formFields = new HashMap<>();
            Part imagePart = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");
                    formFields.put(fieldName, fieldValue);
                } else if ("image".equals(item.getFieldName())) {
                    if (item.getSize() > 0 && item.getName() != null && !item.getName().isEmpty()) {
                        imagePart = new FileItemPart(item);
                    }
                }
            }

            // Required fields for update (id is required)
            String[] requiredFields = {
                "id", "contestId", "userId", "name", "ingredients", "prepareTime",
                "cookingTime", "yield", "category", "area", "shortDescription", "contestEntryInstructions"
            };
            for (String field : requiredFields) {
                if (!formFields.containsKey(field) || formFields.get(field) == null || formFields.get(field).trim().isEmpty()) {
                    sendResponse(resp, error(400, "Missing or empty field: " + field));
                    return;
                }
            }
            // imagePart is optional for update, so don't check for null

            // Parse fields
            int id = Integer.parseInt(formFields.get("id"));
            int contestId = Integer.parseInt(formFields.get("contestId"));
            int userId = Integer.parseInt(formFields.get("userId"));
            String name = formFields.get("name");
            String ingredients = formFields.get("ingredients");
            String prepareTime = formFields.get("prepareTime");
            String cookingTime = formFields.get("cookingTime");
            String yield = formFields.get("yield");
            String category = formFields.get("category");
            String area = formFields.get("area");
            String shortDescription = formFields.get("shortDescription");
            String json = formFields.get("contestEntryInstructions");

            // Parse JSON instructions
            Type listType = new TypeToken<List<ContestEntryInstruction>>() {}.getType();
            List<ContestEntryInstruction> instructions = new ArrayList<>();
            if (json != null && !json.trim().isEmpty()) {
                instructions = GsonUtils.getGson().fromJson(json, listType);
            }

            // Set values to ContestEntryRequest
            ContestEntryRequest contestEntryRequest = new ContestEntryRequest();
            contestEntryRequest.setId(id);
            contestEntryRequest.setContestId(contestId);
            contestEntryRequest.setUserId(userId);
            contestEntryRequest.setName(name);
            contestEntryRequest.setIngredients(ingredients);
            contestEntryRequest.setPrepareTime(prepareTime);
            contestEntryRequest.setCookingTime(cookingTime);
            contestEntryRequest.setYield(yield);
            contestEntryRequest.setCategory(category);
            contestEntryRequest.setArea(area);
            contestEntryRequest.setShortDescription(shortDescription);
            contestEntryRequest.setContestEntryInstructions(instructions);

            // Validate input (imagePart is optional for update)
            ContestEntryRequestValidator validator = new ContestEntryRequestValidator();
            Map<String, String> errors = validator.validateContestEntryRequest(contestEntryRequest, imagePart, true);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            contestEntryService.updateContestEntry(contestEntryRequest, imagePart);
            sendResponse(resp, success(200, "Contest entry updated successfully", null));

        } catch (BadRequestException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload: " + e.getMessage()));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data: " + e.getMessage()));
        } catch (FileUploadException e) {
            sendResponse(resp, error(400, "File upload error: " + e.getMessage()));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid number format: " + e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read JSON payload
            String json = readRequestBody(req);

            // Parse JSON
            DeleteContestEntryRequest deleteContestEntryRequest = GsonUtils.fromJson(json, DeleteContestEntryRequest.class);

            // Validate input
            DeleteContestEntryRequestValidator validator = new DeleteContestEntryRequestValidator();
            Map<String, String> errors = validator.validate(deleteContestEntryRequest);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            contestEntryService.deleteContestEntry(deleteContestEntryRequest);
            sendResponse(resp, success(200, "Contest entry deleted successfully"));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid ID format"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
