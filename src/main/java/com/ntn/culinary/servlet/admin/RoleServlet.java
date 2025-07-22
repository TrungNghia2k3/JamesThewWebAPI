package com.ntn.culinary.servlet.admin;

import com.ntn.culinary.dao.RoleDao;
import com.ntn.culinary.dao.UserRolesDao;
import com.ntn.culinary.dao.impl.RoleDaoImpl;
import com.ntn.culinary.dao.impl.UserRolesDaoImpl;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.request.RoleRequest;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.RoleResponse;
import com.ntn.culinary.service.RoleService;
import com.ntn.culinary.service.impl.RoleServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/admin/roles")
public class RoleServlet extends HttpServlet {
    private final RoleService roleService;

    public RoleServlet() {
        // Inject RoleService
        RoleDao roleDao = new RoleDaoImpl(); // Assuming RoleDao is implemented
        UserRolesDao userRolesDao = new UserRolesDaoImpl(); // Assuming UserRolesDao is implemented
        this.roleService = new RoleServiceImpl(roleDao, userRolesDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Lấy thông tin từ request
            String idParam = req.getParameter("id");
            String nameParam = req.getParameter("name");

            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                // Lấy thông tin role theo ID
                RoleResponse role = roleService.getRoleById(id);
                sendResponse(resp, success(200, "Role fetched successfully", role));
            } else if (nameParam != null) {
                // Lấy thông tin role theo tên
                RoleResponse role = roleService.getRoleByName(nameParam);
                sendResponse(resp, success(200, "Roles fetched successfully", role));
            } else {
                // Trả về danh sách tất cả các role
                List<RoleResponse> roles = roleService.getAllRoles();
                sendResponse(resp, success(200, "All roles fetched successfully", roles));
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

            // Parse JSON to get role name
            RoleRequest roleRequest = fromJson(json, RoleRequest.class);

            // Validate role name
            String name = roleRequest.getName();
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Role name can not be null or empty");
            }

            roleService.addRole(name);
            sendResponse(resp, success(201, "Role added successfully"));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read JSON body from request
            String json = readRequestBody(req);

            // Parse JSON to get role details
            RoleRequest roleRequest = fromJson(json, RoleRequest.class);

            // Validate role ID and name
            if (roleRequest.getId() <= 0 || roleRequest.getName() == null || roleRequest.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Role ID and name can not be null or empty");
            }

            roleService.updateRole(roleRequest);
            sendResponse(resp, success(200, "Role updated successfully"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
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
                throw new IllegalArgumentException("Role ID is required");
            }

            int id = Integer.parseInt(idParam);
            roleService.deleteRole(id);
            sendResponse(resp, success(200, "Role deleted successfully"));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid ID format"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
