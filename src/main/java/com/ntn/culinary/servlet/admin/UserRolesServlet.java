package com.ntn.culinary.servlet.admin;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.RoleDao;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.dao.UserRolesDao;
import com.ntn.culinary.dao.impl.RoleDaoImpl;
import com.ntn.culinary.dao.impl.UserDaoImpl;
import com.ntn.culinary.dao.impl.UserRolesDaoImpl;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.request.UserRolesRequest;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.service.UserRolesService;
import com.ntn.culinary.service.impl.UserRolesServiceImpl;

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

@WebServlet("/api/protected/admin/user-roles")
public class UserRolesServlet extends HttpServlet {
    private final UserRolesService userRolesService;

    public UserRolesServlet() {
        UserRolesDao userRolesDao = new UserRolesDaoImpl();
        UserDao userDao = new UserDaoImpl();
        RoleDao roleDao = new RoleDaoImpl();

        this.userRolesService = new UserRolesServiceImpl(userRolesDao, userDao, roleDao);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read request body and build JSON string
            String json = readRequestBody(req);

            // Parse JSON sting to UserRolesRequest object by Gson
            UserRolesRequest userRolesRequest = fromJson(json, UserRolesRequest.class);

            int userId = userRolesRequest.getUserId();
            int roleId = userRolesRequest.getRoleId();

            // Validate userId and roleId
            if (userId <= 0 || roleId <= 0) {
                throw new IllegalArgumentException("Invalid userId or roleId");
            }

            userRolesService.assignRoleToUser(userId, roleId);
            sendResponse(resp, success(201, "Role assigned successfully"));
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

            // Parse JSON sting to UserRolesRequest object by Gson
            UserRolesRequest userRolesRequest = fromJson(json, UserRolesRequest.class);

            int userId = userRolesRequest.getUserId();
            int roleId = userRolesRequest.getRoleId();

            // Validate userId and roleId
            if (userId <= 0 || roleId <= 0) {
                throw new IllegalArgumentException("Invalid userId or roleId");
            }

            userRolesService.removeRoleFromUser(userId, roleId);
            sendResponse(resp, success(200, "Role removed successfully"));
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

