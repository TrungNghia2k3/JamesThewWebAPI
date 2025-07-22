package com.ntn.culinary.servlet;

import com.ntn.culinary.exception.BadRequestException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.ntn.culinary.response.ApiResponse.error;

@WebServlet("/api/images/*")
public class ImageServlet extends HttpServlet {

    private static final String BASE_PATH = "E:/Project/JamesThewWebApplication/source-code/backend/images/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo(); // Ví dụ: /recipes/abc.jpg hoặc /avatars/def.png

            if (pathInfo == null || pathInfo.equals("/")) {
                throw new BadRequestException("Path is required");
            }

            String[] parts = pathInfo.split("/", 3); // [ "", "recipes", "abc.jpg" ]
            if (parts.length < 3) {
                throw new BadRequestException("Invalid path. Must include type and filename");
            }

            String type = parts[1];      // "recipes" hoặc "avatars"
            String filename = parts[2];  // "abc.jpg"

            File imageFile = new File(BASE_PATH + type, filename);

            if (!imageFile.exists()) {
                throw new NotFoundException("Image not found");
            }

            String mimeType = getServletContext().getMimeType(imageFile.getName());
            if (mimeType == null) mimeType = "application/octet-stream";

            resp.setContentType(mimeType);
            resp.setContentLengthLong(imageFile.length());

            try (FileInputStream in = new FileInputStream(imageFile);
                 OutputStream out = resp.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (BadRequestException e) {
            ResponseUtils.sendResponse(resp, error(400, e.getMessage()));
        } catch (NotFoundException e) {
            ResponseUtils.sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            ResponseUtils.sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}



