package com.ntn.culinary.utils;

import com.ntn.culinary.response.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ntn.culinary.utils.GsonUtils.toJson;

public class ResponseUtils {
    public static void sendResponse(HttpServletResponse resp, ApiResponse<?> response) {
        resp.setContentType("application/json");
        resp.setStatus(response.getStatus());
        try {
            resp.getWriter().write(toJson(response));
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while writing response", e);
        }
    }
}
