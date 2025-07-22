package com.ntn.culinary.servlet.staff;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.dao.ContestImagesDao;
import com.ntn.culinary.dao.impl.ContestDaoImpl;
import com.ntn.culinary.dao.impl.ContestImagesDaoImpl;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.exception.ValidationException;
import com.ntn.culinary.request.ContestImagesRequest;
import com.ntn.culinary.request.ContestRequest;
import com.ntn.culinary.service.ContestService;
import com.ntn.culinary.service.impl.ContestServiceImpl;
import com.ntn.culinary.validator.ContestRequestValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.ntn.culinary.response.ApiResponse.*;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/protected/staff/contests")
public class ContestServlet extends HttpServlet {

    private final ContestService contestService;

    public ContestServlet() {
        ContestDao contestDao = new ContestDaoImpl();
        ContestImagesDao contestImagesDao = new ContestImagesDaoImpl();
        this.contestService = new ContestServiceImpl(contestDao, contestImagesDao);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("utf-8");

            // Lấy dữ liệu từ form
            String headline = req.getParameter("headline");
            String articleBody = req.getParameter("articleBody");
            String description = req.getParameter("description");
            String prize = req.getParameter("prize");
            boolean isFree = Boolean.parseBoolean(req.getParameter("isFree"));
            String accessRole = req.getParameter("accessRole");

            // private List<ContestImages> contestImages;
            // Giả sử contestImages được lấy từ một phần khác của request, ví dụ từ multipart/form-data
            List<ContestImagesRequest> contestImages = getContestImagesFromRequest(req);

            // Create ContestRequest object
            ContestRequest contestRequest = new ContestRequest();
            contestRequest.setHeadline(headline);
            contestRequest.setArticleBody(articleBody);
            contestRequest.setDescription(description);
            contestRequest.setPrize(prize);
            contestRequest.setFree(isFree);
            contestRequest.setAccessRole(accessRole);
            contestRequest.setContestImages(contestImages);

            // Validate the request
            ContestRequestValidator validator = new ContestRequestValidator();
            Map<String, String> errors = validator.validate(contestRequest, false);
            if (!errors.isEmpty()) {
                throw new ValidationException("Validation failed", errors);
            }

            // Add the contest
            contestService.addContest(contestRequest);
            sendResponse(resp, success(201, "Contest created successfully"));
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
            req.setCharacterEncoding("utf-8");

            // Lấy ID từ URL
            String idParam = req.getParameter("id");
            boolean status = Boolean.parseBoolean(req.getParameter("status"));


            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID is required for update");
            }

            int id = Integer.parseInt(idParam);

            // Nếu tồn tại cả id và status
            if (id > 0 && status) {
                // Cập nhật trạng thái cuộc thi
                contestService.updateContestStatus(id);
                sendResponse(resp, success(200, "Contest status updated successfully"));
                return;
            }

            // Nếu chỉ tồn tại id
            else if (id > 0) {
                // Lấy dữ liệu từ form
                String headline = req.getParameter("headline");
                String articleBody = req.getParameter("articleBody");
                String description = req.getParameter("description");
                String prize = req.getParameter("prize");
                boolean isFree = Boolean.parseBoolean(req.getParameter("isFree"));
                String accessRole = req.getParameter("accessRole");

                // private List<ContestImages> contestImages;
                // Giả sử contestImages được lấy từ một phần khác của request, ví dụ từ multipart/form-data
                List<ContestImagesRequest> contestImages = getContestImagesFromRequest(req);

                // Create ContestRequest object
                ContestRequest contestRequest = new ContestRequest();
                contestRequest.setId(id);
                contestRequest.setHeadline(headline);
                contestRequest.setArticleBody(articleBody);
                contestRequest.setDescription(description);
                contestRequest.setPrize(prize);
                contestRequest.setFree(isFree);
                contestRequest.setAccessRole(accessRole);
                contestRequest.setContestImages(contestImages);

                // Validate the request
                ContestRequestValidator validator = new ContestRequestValidator();
                Map<String, String> errors = validator.validate(contestRequest, true);
                if (!errors.isEmpty()) {
                    throw new ValidationException("Validation failed", errors);
                }

                // Update the contest
                contestService.updateContest(contestRequest);
                sendResponse(resp, error(200, "Contest updated successfully"));
            }
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, e.getMessage()));
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("utf-8");

            // Lấy ID từ URL
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("ID is required for deletion");
            }
            int id = Integer.parseInt(idParam);

            // Xóa cuộc thi
            contestService.deleteContest(id);
            sendResponse(resp, success(200, "Contest deleted successfully"));

        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    private List<ContestImagesRequest> getContestImagesFromRequest(HttpServletRequest req)
            throws ServletException, IOException {
        List<ContestImagesRequest> contestImages = new ArrayList<>();

        Collection<Part> parts = req.getParts();
        int order = 1;

        for (Part part : parts) {
            // Lấy tất cả part có name="contestImages"
            if ("contestImages".equals(part.getName()) && part.getSize() > 0) {
                ContestImagesRequest contestImage = new ContestImagesRequest();
                contestImage.setImage(part);
                contestImages.add(contestImage);
            }
        }

        return contestImages;
    }
}
