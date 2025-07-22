package com.ntn.culinary.servlet.admin;

import com.ntn.culinary.dao.PermissionDao;
import com.ntn.culinary.dao.StaffPermissionsDao;
import com.ntn.culinary.dao.impl.PermissionDaoImpl;
import com.ntn.culinary.dao.impl.StaffPermissionsDaoImpl;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.request.PermissionRequest;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.PermissionResponse;
import com.ntn.culinary.service.PermissionService;
import com.ntn.culinary.service.impl.PermissionServiceImpl;
import com.ntn.culinary.validator.PermissionRequestValidator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/admin/permissions")
public class PermissionServlet extends HttpServlet {
    private final PermissionService permissionService;

    public PermissionServlet() {
        // Inject RoleService
        PermissionDao permissionDao = new PermissionDaoImpl(); // Assuming RoleDao is implemented
        StaffPermissionsDao staffPermissionsDao = new StaffPermissionsDaoImpl() {
        }; // Assuming UserRolesDao is implemented
        this.permissionService = new PermissionServiceImpl(staffPermissionsDao, permissionDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Lấy thông tin từ request
            String idParam = req.getParameter("id");
            String nameParam = req.getParameter("name");

            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                // Lấy thông tin permission theo ID
                PermissionResponse permission = permissionService.getPermissionById(id);
                sendResponse(resp, success(200, "Permission fetched successfully", permission));
            } else if (nameParam != null) {
                // Lấy thông tin permission theo tên
                PermissionResponse permission = permissionService.getPermissionByName(nameParam);
                sendResponse(resp, success(200, "Permission fetched successfully", permission));
            } else {
                // Trả về danh sách tất cả các permission
                List<PermissionResponse> permissions = permissionService.getAllPermissions();
                sendResponse(resp, success(200, "All permissions fetched successfully", permissions));
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
            // Read JSON body from request
            String json = readRequestBody(req);

            // Parse JSON to get role request
            PermissionRequest permissionRequest = fromJson(json, PermissionRequest.class);

            // Validate role request
            PermissionRequestValidator validator = new PermissionRequestValidator();
            Map<String, String> errors = validator.validate(permissionRequest, true);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            permissionService.addPermission(permissionRequest);
            sendResponse(resp, success(201, "Permission added successfully"));
        }catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
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
            // Read JSON body from request
            String json = readRequestBody(req);

            // Parse JSON to get role request
            PermissionRequest permissionRequest = fromJson(json, PermissionRequest.class);

            // Validate role request
            PermissionRequestValidator validator = new PermissionRequestValidator();
            Map<String, String> errors = validator.validate(permissionRequest, true);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            permissionService.updatePermission(permissionRequest);
            sendResponse(resp, success(200, "Permission updated successfully"));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
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
            // Lấy ID từ request
            String idParam = req.getParameter("id");
            if (idParam == null) {
                throw new IllegalArgumentException("ID parameter is required");
            }

            int id = Integer.parseInt(idParam);
            permissionService.deletePermissionById(id);
            sendResponse(resp, success(200, "Permission deleted successfully"));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid ID format"));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
