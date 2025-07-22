package com.ntn.culinary.servlet;

import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.dao.ContestImagesDao;
import com.ntn.culinary.dao.impl.ContestDaoImpl;
import com.ntn.culinary.dao.impl.ContestImagesDaoImpl;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.ContestResponse;
import com.ntn.culinary.service.ContestService;
import com.ntn.culinary.service.impl.ContestServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;


@WebServlet("/api/contests")
public class ContestServlet extends HttpServlet {
    private final ContestService contestService;

    public ContestServlet() {
        ContestDao contestDao = new ContestDaoImpl();
        ContestImagesDao contestImagesDao = new ContestImagesDaoImpl();
        this.contestService = new ContestServiceImpl(contestDao, contestImagesDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Get the 'id' parameter from the request
            String idParam = req.getParameter("id");

            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                ContestResponse contest = contestService.getContestById(id);
                sendResponse(resp, success(200, "Contest fetched successfully", contest));
            } else {
                List<ContestResponse> contests = contestService.getAllContests();
                sendResponse(resp, success(200, "All contests fetched", contests));
            }
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid ID format"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}




