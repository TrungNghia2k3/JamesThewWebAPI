package com.ntn.culinary.servlet;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.dao.impl.UserDaoImpl;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.request.RegisterRequest;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.service.ImageService;
import com.ntn.culinary.service.UserService;
import com.ntn.culinary.service.impl.ImageServiceImpl;
import com.ntn.culinary.service.impl.UserServiceImpl;
import com.ntn.culinary.validator.RegisterRequestValidator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService;

    public RegisterServlet() {
        UserDao userDao = new UserDaoImpl();
        ImageService imageService = new ImageServiceImpl();
        this.userService = new UserServiceImpl(userDao, imageService);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String json = readRequestBody(req);

            // Parse JSON string to RegisterRequest object using Gson
            RegisterRequest userRequest = fromJson(json, RegisterRequest.class);

            // Validate input
            RegisterRequestValidator validator = new RegisterRequestValidator();
            Map<String, String> errors = validator.validate(userRequest);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            userService.register(userRequest);
            sendResponse(resp, success(201, "User created successfully", null));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(400, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
