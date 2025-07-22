package com.ntn.culinary.servlet;

import com.ntn.culinary.dao.AreaDao;
import com.ntn.culinary.dao.impl.AreaDaoImpl;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.response.AreaResponse;
import com.ntn.culinary.service.AreaService;
import com.ntn.culinary.service.impl.AreaServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.ntn.culinary.response.ApiResponse.error;
import static com.ntn.culinary.response.ApiResponse.success;
import static com.ntn.culinary.utils.ResponseUtils.sendResponse;

@WebServlet("/api/areas")
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
}
