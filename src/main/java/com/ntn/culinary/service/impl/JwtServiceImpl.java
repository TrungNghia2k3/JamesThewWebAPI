package com.ntn.culinary.service.impl;

import com.ntn.culinary.model.Permission;
import com.ntn.culinary.model.Role;
import com.ntn.culinary.model.User;
import com.ntn.culinary.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ntn.culinary.utils.CastUtils.toStringList;

public class JwtServiceImpl implements JwtService {
    private static final String SECRET_KEY = System.getenv("SECRET_KEY");
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    @Override
    public String generateJwt(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissionNames = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("user_id", user.getId())
                .claim("roles", roleNames)
                .claim("permissions", permissionNames)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    @Override
    public Claims validateJwt(String jwt) {
        assert SECRET_KEY != null;
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    @Override
    public boolean hasRequiredRole(HttpServletRequest request, List<String> userRoles) {
        // Loại bỏ contextPath khỏi URI
        String uri = request.getRequestURI().substring(request.getContextPath().length());

        List<String> requiredRoles = getRequiredRoles(uri);

        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }

        for (String role : userRoles) {
            if (requiredRoles.contains(role.toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(HttpServletRequest request, Claims claims, String permission) {
        // Nếu user có vai trò ADMIN thì luôn cho phép
        List<String> roles = toStringList(request.getAttribute("roles"));
        if (roles != null && roles.contains("ADMIN")) {
            return true;
        }

        List<String> permissions = claims.get("permissions", List.class);
        return permissions != null && permissions.contains(permission);
    }

    private List<String> getRequiredRoles(String uri) {
        if (uri.startsWith("/api/protected/admin")) {
            return List.of("ADMIN"); // chỉ ADMIN
        } else if (uri.startsWith("/api/protected/staff")) {
            return List.of("ADMIN", "STAFF", "WRITER");
        } else if (uri.startsWith("/api/protected/writer")) {
            return List.of("ADMIN", "STAFF", "WRITER");
        } else if (uri.startsWith("/api/protected/subscriber")) {
            return List.of("ADMIN", "STAFF", "WRITER", "SUBSCRIBER");
        } else if (uri.startsWith("/api/protected/general")) {
            return List.of("ADMIN", "STAFF", "WRITER", "SUBSCRIBER", "GENERAL"); // mọi role
        }
        return null; // nếu không yêu cầu role nào cả
    }
}
