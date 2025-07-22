package com.ntn.culinary.filter;

import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.dao.impl.UserDaoImpl;
import com.ntn.culinary.exception.ForbiddenException;
import com.ntn.culinary.exception.UnauthorizedException;
import com.ntn.culinary.response.ApiResponse;
import com.ntn.culinary.service.JwtService;
import com.ntn.culinary.service.UserService;
import com.ntn.culinary.service.impl.JwtServiceImpl;
import com.ntn.culinary.service.impl.UserServiceImpl;
import com.ntn.culinary.utils.ResponseUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ntn.culinary.response.ApiResponse.error;

@WebFilter(urlPatterns = "/api/protected/*")
public class JwtFilter implements Filter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtFilter() {
        UserDao userDao = new UserDaoImpl();
        this.jwtService = new JwtServiceImpl();
        this.userService = new UserServiceImpl(userDao);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            // Kiểm tra phương thức HTTP
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Authorization header is missing or invalid");
            }

            // Lấy JWT từ header
            String jwt = authHeader.substring(7);
            Claims claims = jwtService.validateJwt(jwt);

            // Lấy thông tin người dùng từ claims
            Integer userId = claims.get("user_id", Integer.class);
            String username = claims.getSubject();

            List<?> rolesRaw = claims.get("roles", List.class);
            List<String> roles = rolesRaw != null ? rolesRaw.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString) // an toàn ép kiểu
                    .collect(Collectors.toList()) : Collections.emptyList();

            List<?> permissionsRaw = claims.get("permissions", List.class);
            List<String> permissions = permissionsRaw != null ? permissionsRaw.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList()) : Collections.emptyList();

            request.setAttribute("user_id", userId);
            request.setAttribute("username", username);
            request.setAttribute("roles", roles);
            request.setAttribute("permissions", permissions);

            // Nếu là MEMBER thì kiểm tra subscription
//            if (roles.contains("MEMBER")) {
//                boolean isSubscriptionValid = userService.isSubscriptionValid(userId);
//                if (!isSubscriptionValid) {
//                    ResponseUtil.sendResponse(response, error(403, "Subscription expired or user not found"));
//                    return;
//                }
//            }

            // Kiểm tra role có đủ quyền truy cập không
            if (!jwtService.hasRequiredRole(request, roles)) {
                throw new ForbiddenException("Access denied: insufficient role");
            }

            // Kiểm tra permission cho các API nhạy cảm
            String uri = request.getRequestURI().substring(request.getContextPath().length());

            // Duyệt qua các API nhạy cảm và kiểm tra permission
            for (Map.Entry<String, String> entry : PERMISSION_MAP.entrySet()) {
                if (uri.startsWith(entry.getKey())) {
                    String requiredPermission = entry.getValue();
                    if (!jwtService.hasPermission(request, claims, requiredPermission)) {
                        throw new ForbiddenException("Access denied: " + requiredPermission + " permission required");
                    }
                    break;
                }
            }

            // (Optional) Nếu muốn kiểm tra permission:
            // if (!jwtService.hasPermission(claims, "MANAGE_CONTESTS")) {
            //     ResponseUtil.sendResponse(response, error(403, "Permission denied"));
            //     return;
            // }

            chain.doFilter(req, res);
        } catch (UnauthorizedException e) {
            ResponseUtils.sendResponse(response, error(401, "Unauthorized: " + e.getMessage()));
        } catch (ExpiredJwtException e) {
            ResponseUtils.sendResponse(response, error(401, "Unauthorized: token expired"));
        } catch (JwtException e) {
            ResponseUtils.sendResponse(response, error(401, "Unauthorized: invalid token"));
        } catch (ForbiddenException e) {
            ResponseUtils.sendResponse(response, error(403, e.getMessage()));
        } catch (Exception e) {
            ResponseUtils.sendResponse(response, error(500, "Server error: " + e.getMessage()));
        }
    }

    private static final Map<String, String> PERMISSION_MAP = Map.of(
            "/api/protected/staff/contests", "MANAGE_CONTESTS",
            "/api/protected/staff/users", "MANAGE_USERS",
            "/api/protected/staff/contest-entries","MANAGE_CONTEST_ENTRIES",
            "/api/protected/staff/score-contest-entry-examiners", "MANAGE_SCORE_CONTEST_ENTRIES",
            "/api/protected/staff/comments","MANAGE_COMMENTS"
    );
}
