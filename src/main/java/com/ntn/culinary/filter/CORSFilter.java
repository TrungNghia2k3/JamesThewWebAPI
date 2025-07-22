package com.ntn.culinary.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Filter áp dụng cho TẤT CẢ request (/*) đến server
@WebFilter("/*")
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // Khởi tạo filter
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // CORS: Cho phép frontend từ localhost:5173 gọi API
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");

        // Cho phép các phương thức HTTP
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Cho phép các header
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization");

        // Cho phép gửi credentials (cookies, authorization headers)
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // Xử lý preflight request (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Tiếp tục với request chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup khi filter bị hủy
    }
}
