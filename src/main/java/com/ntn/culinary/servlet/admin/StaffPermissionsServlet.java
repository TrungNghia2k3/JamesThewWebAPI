package com.ntn.culinary.servlet.admin;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.PermissionDao;
import com.ntn.culinary.dao.RoleDao;
import com.ntn.culinary.dao.StaffPermissionsDao;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.dao.impl.PermissionDaoImpl;
import com.ntn.culinary.dao.impl.RoleDaoImpl;
import com.ntn.culinary.dao.impl.StaffPermissionsDaoImpl;
import com.ntn.culinary.dao.impl.UserDaoImpl;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.request.StaffPermissionsRequest;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.service.StaffPermissionsService;
import com.ntn.culinary.service.impl.StaffPermissionsServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/admin/staff-permissions")
public class StaffPermissionsServlet extends HttpServlet {
    private final StaffPermissionsService staffPermissionsService;

    public StaffPermissionsServlet() {
        StaffPermissionsDao staffPermissionsDao = new StaffPermissionsDaoImpl();
        UserDao userDao = new UserDaoImpl();
        PermissionDao permissionDao = new PermissionDaoImpl();
        this.staffPermissionsService = new StaffPermissionsServiceImpl(staffPermissionsDao, userDao, permissionDao);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read request body and build JSON string
            String json = readRequestBody(req);

            // Parse JSON sting to StaffPermissionsRequest object by Gson
            StaffPermissionsRequest staffPermissionsRequest = fromJson(json, StaffPermissionsRequest.class);

            int userId = staffPermissionsRequest.getUserId();
            int permissionId = staffPermissionsRequest.getPermissionId();

            // Validate userId and roleId
            if (userId <= 0 || permissionId <= 0) {
                throw new IllegalArgumentException("Invalid userId or permissionId");
            }

            staffPermissionsService.assignPermissionToStaff(userId, permissionId);
            sendResponse(resp, success(200, "Permission assigned successfully"));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Database error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read request body and build JSON string
            String json = readRequestBody(req);

            // Parse JSON sting to StaffPermissionsRequest object by Gson
            StaffPermissionsRequest staffPermissionsRequest = fromJson(json, StaffPermissionsRequest.class);

            int userId = staffPermissionsRequest.getUserId();
            int permissionId = staffPermissionsRequest.getPermissionId();

            // Validate userId and roleId
            if (userId <= 0 || permissionId <= 0) {
                throw new IllegalArgumentException("Invalid userId or permissionId");
            }

            staffPermissionsService.removePermissionFromStaff(userId, permissionId);
            sendResponse(resp, success(200, "Permission removed successfully"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Database error: " + e.getMessage()));
        }
    }
}
