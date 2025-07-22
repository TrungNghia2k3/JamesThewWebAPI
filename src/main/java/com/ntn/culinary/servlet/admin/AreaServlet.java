package com.ntn.culinary.servlet.admin;

import com.google.gson.JsonSyntaxException;
import com.ntn.culinary.dao.AreaDao;
import com.ntn.culinary.dao.impl.AreaDaoImpl;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.request.AreaRequest;
import com.ntn.culinary.response.AreaResponse;
import com.ntn.culinary.service.AreaService;
import com.ntn.culinary.service.impl.AreaServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.GsonUtils.fromJson;
import static com.ntn.culinary.utils.HttpRequestUtils.readRequestBody;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;
import static com.ntn.culinary.utils.ValidationUtils.isNotExistId;
import static com.ntn.culinary.utils.ValidationUtils.isNullOrEmpty;

@WebServlet("/api/protected/admin/areas")
public class AreaServlet extends HttpServlet {

    private final AreaService areaService;

    public AreaServlet() {
        // Inject AreaDaoImpl
        AreaDao areaDao = new AreaDaoImpl();
        this.areaService = new AreaServiceImpl(areaDao);
    }

    // Có nghĩa:
    // Tự chịu trách nhiệm tạo dependency (AreaDaoImpl, AreaService)
    // Servlet container (Tomcat) chỉ cần gọi constructor mặc định không tham số (), nó biết cách khởi tạo servlet.
    // Đây là cách truyền thống, hoạt động ngay lập tức mà không cần framework hỗ trợ Dependency Injection.

    //  Nếu viết constructor có tham số thì sao?
    //Vấn đề ở đây:
    //Tomcat KHÔNG biết làm sao để cung cấp AreaService vào tham số constructor.
    //Servlet container chỉ hỗ trợ constructor mặc định không tham số khi tự động khởi tạo servlet.
    //Nó không tự inject dependency qua constructor như Spring.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Lấy thông tin từ request
            String idParam = req.getParameter("id");
            String nameParam = req.getParameter("name");

            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                AreaResponse area = areaService.getAreaById(id);
                sendResponse(resp, success(200, "Area fetched successfully", area));
            } else if (nameParam != null) {
                // Lấy danh sách các khu vực theo tên
                AreaResponse area = areaService.getAreaByName(nameParam);
                if (area == null) {
                    sendResponse(resp, error(404, "Area not found"));
                } else {
                    sendResponse(resp, success(200, "Area fetched successfully", area));
                }
            } else {
                List<AreaResponse> areas = areaService.getAllAreas();
                sendResponse(resp, success(200, "All areas fetched successfully", areas));
            }
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid ID format"));
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
            AreaRequest areaRequest = fromJson(json, AreaRequest.class);

            // Validate input
            if (isNullOrEmpty(areaRequest.getName())) {
                throw new IllegalArgumentException("Area name cannot be null or empty");
            }

            areaService.addArea(areaRequest);
            sendResponse(resp, success(200, "Area added successfully"));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, "Invalid request: " + e.getMessage()));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
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
            AreaRequest areaRequest = fromJson(json, AreaRequest.class);

            // Validate input
            if (isNotExistId(areaRequest.getId()) || isNullOrEmpty(areaRequest.getName())) {
                throw new IllegalArgumentException("Invalid area ID or name");
            }

            areaService.updateArea(areaRequest);
            sendResponse(resp, success(200, "Area updated successfully"));
        } catch (JsonSyntaxException e) {
            sendResponse(resp, error(400, "Invalid JSON data"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, "Invalid request: " + e.getMessage()));
        } catch (IOException e) {
            sendResponse(resp, error(400, "Invalid request payload"));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (ConflictException e) {
            sendResponse(resp, error(409, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Get area ID from request parameters
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new IllegalArgumentException("Area ID is required");
            }

            int id = Integer.parseInt(idParam);
            areaService.deleteAreaById(id);
            sendResponse(resp, success(200, "Area deleted successfully"));
        } catch (NumberFormatException e) {
            sendResponse(resp, error(400, "Invalid area ID format"));
        } catch (IllegalArgumentException e) {
            sendResponse(resp, error(400, "Invalid request: " + e.getMessage()));
        } catch (NotFoundException e) {
            sendResponse(resp, error(404, e.getMessage()));
        } catch (Exception e) {
            sendResponse(resp, error(500, "Server error: " + e.getMessage()));
        }
    }
}
