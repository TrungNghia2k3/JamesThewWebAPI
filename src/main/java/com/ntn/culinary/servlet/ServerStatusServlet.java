package com.ntn.culinary.servlet;

import com.ntn.culinary.response.ApiResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/server-status")
public class ServerStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Simple health check endpoint
        sendResponse(resp, ApiResponse.success(200, "Server is running", "OK"));
    }
}

