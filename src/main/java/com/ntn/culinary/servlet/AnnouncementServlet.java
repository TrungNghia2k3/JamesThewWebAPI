package com.ntn.culinary.servlet;

import com.ntn.culinary.dao.AnnounceWinnerDao;
import com.ntn.culinary.dao.AnnouncementDao;
import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.dao.ContestEntryDao;
import com.ntn.culinary.dao.impl.AnnounceWinnerDaoImpl;
import com.ntn.culinary.dao.impl.AnnouncementDaoImpl;
import com.ntn.culinary.dao.impl.ContestDaoImpl;
import com.ntn.culinary.dao.impl.ContestEntryDaoImpl;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.response.AnnouncementResponse;
import com.ntn.culinary.service.AnnouncementService;
import com.ntn.culinary.service.impl.AnnouncementServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/announcements")
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
}
