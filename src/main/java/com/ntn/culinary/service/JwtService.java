package com.ntn.culinary.service;

import com.ntn.culinary.model.User;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface JwtService {
    String generateJwt(User user);

    Claims validateJwt(String jwt);

    boolean hasRequiredRole(HttpServletRequest request, List<String> userRoles);

    boolean hasPermission(HttpServletRequest request, Claims claims, String permission);
}
