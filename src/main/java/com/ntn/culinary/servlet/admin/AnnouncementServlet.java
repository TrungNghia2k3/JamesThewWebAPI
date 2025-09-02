package com.ntn.culinary.servlet.admin;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.AnnounceWinnerDao;
import com.ntn.culinary.dao.AnnouncementDao;
import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.dao.ContestEntryDao;
import com.ntn.culinary.dao.impl.AnnounceWinnerDaoImpl;
import com.ntn.culinary.dao.impl.AnnouncementDaoImpl;
import com.ntn.culinary.dao.impl.ContestDaoImpl;
import com.ntn.culinary.dao.impl.ContestEntryDaoImpl;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.ForbiddenException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.request.AnnouncementRequest;
import com.ntn.culinary.response.AnnouncementResponse;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.service.AnnouncementService;
import com.ntn.culinary.service.impl.AnnouncementServiceImpl;
import com.ntn.culinary.validator.AnnouncementRequestValidator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/admin/announcements")
public class AnnouncementServlet extends HttpServlet {

    private final AnnouncementService announcementService;

    public AnnouncementServlet() {
        ContestDao contestDao = new ContestDaoImpl();
        AnnouncementDao announcementDao = new AnnouncementDaoImpl();
        AnnounceWinnerDao announceWinnerDao = new AnnounceWinnerDaoImpl();
        ContestEntryDao contestEntryDao = new ContestEntryDaoImpl();

        this.announcementService = new AnnouncementServiceImpl(contestDao, announcementDao, announceWinnerDao, contestEntryDao);
    }

    // Xem thông tin thông báo, có thể lọc theo ID, có thể lấy tất cả thông báo, chỉnh sửa thông báo, xóa thông báo

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Get announcement ID from request parameters
            String idParam = req.getParameter("id");

            if (idParam != null) {
                int announcementId = Integer.parseInt(idParam);
                AnnouncementResponse announcement = announcementService.getAnnouncementById(announcementId);
                sendResponse(resp, success(200, "Announcement found", announcement));
            } else {
                List<AnnouncementResponse> announcements = announcementService.getAllAnnouncements();
                if (announcements.isEmpty()) {
                    sendResponse(resp, error(404, "No announcements found"));
                } else {
                    sendResponse(resp, success(200, "Announcements fetched successfully", announcements));
                }
            }
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid announcement ID format"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read JSON payload
            String json = readRequestBody(req);

            // Parse JSON
            AnnouncementRequest announcementRequest = fromJson(json, AnnouncementRequest.class);

            // Validate input
            AnnouncementRequestValidator validator = new AnnouncementRequestValidator();
            Map<String, String> errors = validator.validate(announcementRequest, false);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            announcementService.addAnnouncement(announcementRequest);
            sendResponse(resp, success(201, "Announcement created successfully"));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read JSON payload
            String json = readRequestBody(req);

            // Parse JSON
            AnnouncementRequest announcementRequest = fromJson(json, AnnouncementRequest.class);

            // Validate input
            AnnouncementRequestValidator validator = new AnnouncementRequestValidator();
            Map<String, String> errors = validator.validate(announcementRequest, true);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            // Update the announcement
            announcementService.updateAnnouncement(announcementRequest);
            sendResponse(resp, success(200, "Announcement updated successfully"));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (ValidationException e) {
            sendResponse(resp, validationError(422, e.getMessage(), e.getErrors()));
        } catch (ForbiddenException e) {
            sendResponse(resp, error(403, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Get announcement ID from request parameters
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("Announcement ID is required");
            }

            int announcementId = Integer.parseInt(idParam);

            // Delete the announcement
            announcementService.deleteAnnouncement(announcementId);
            sendResponse(resp, success(200, "Announcement deleted successfully"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, "Invalid request: " + e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ForbiddenException e) {
            sendResponse(resp, error(403, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
