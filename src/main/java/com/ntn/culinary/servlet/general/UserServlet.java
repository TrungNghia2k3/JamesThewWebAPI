package com.ntn.culinary.servlet.general;

import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.dao.impl.UserDaoImpl;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.request.UserRequest;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.UserResponse;
import com.ntn.culinary.service.ImageService;
import com.ntn.culinary.service.UserService;
import com.ntn.culinary.service.impl.ImageServiceImpl;
import com.ntn.culinary.service.impl.UserServiceImpl;
import com.ntn.culinary.validator.UpdateUserRequestValidator;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/general/users")
@MultipartConfig
public class UserServlet extends HttpServlet {
    private final UserService userService;

    public UserServlet() {
        UserDao userDao = new UserDaoImpl();
        ImageService imageService = new ImageServiceImpl(); // Assuming you have an ImageService implementation
        this.userService = new UserServiceImpl(userDao, imageService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String idParam = req.getParameter("id");

            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                UserResponse user = userService.getUserById(id);
                sendResponse(resp, success(200, "User found", user));
            }
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");

            // Lấy dữ liệu form
            int id = Integer.parseInt(req.getParameter("id"));
            String email = req.getParameter("email");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            Part avatar = req.getPart("avatar");

            // Create UserRequest object
            UserRequest userRequest = new UserRequest();
            userRequest.setId(id);
            userRequest.setEmail(email);
            userRequest.setFirstName(firstName);
            userRequest.setLastName(lastName);
            userRequest.setPhone(phone);
            userRequest.setAvatar(avatar);

            // Validate input
            UpdateUserRequestValidator validator = new UpdateUserRequestValidator();
            Map<String, String> errors = validator.validate(userRequest);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            userService.updateGeneralUser(userRequest);
            sendResponse(resp, success(200, "User updated successfully", null));

        } catch (ValidationException e) {
            sendResponse(resp, validationError(400, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
