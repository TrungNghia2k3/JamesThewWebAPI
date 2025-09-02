package com.ntn.culinary.servlet;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.dao.impl.UserDaoImpl;
import com.ntn.culinary.exception.BadRequestException;
import com.ntn.culinary.exception.ForbiddenException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.request.LoginRequest;
import com.ntn.culinary.service.AuthService;
import com.ntn.culinary.service.JwtService;
import com.ntn.culinary.service.impl.AuthServiceImpl;
import com.ntn.culinary.service.impl.JwtServiceImpl;
import com.ntn.culinary.validator.LoginRequestValidator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private final AuthService authService;

    public LoginServlet() {
        UserDao userDao = new UserDaoImpl();
        JwtService jwtService = new JwtServiceImpl();
        this.authService = new AuthServiceImpl(userDao, jwtService);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        try {
            // Read request body and build JSON string
            String json = readRequestBody(req);

            // Parse JSON sting to LoginRequest object by Gson
            LoginRequest loginRequest = fromJson(json, LoginRequest.class);

            // Validate input
            LoginRequestValidator validator = new LoginRequestValidator();
            Map<String, String> errors = validator.validate(loginRequest);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            // Authenticate
            String jwt = authService.authenticate(loginRequest);
            if (jwt != null) sendResponse(resp, success(201, "Login successful", jwt));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (BadRequestException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (ForbiddenException e) {
            sendResponse(resp, error(403, e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}