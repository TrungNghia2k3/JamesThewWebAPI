package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.exception.BadRequestException;
import com.ntn.culinary.exception.ForbiddenException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.User;
import com.ntn.culinary.request.LoginRequest;
import com.ntn.culinary.service.AuthService;
import com.ntn.culinary.service.JwtService;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImpl implements AuthService {
    private final UserDao userDao;
    private final JwtService jwtServiceImpl;

    // service giao tiếp với service là bình thường.
    // Miễn là:
    // Trách nhiệm rõ ràng.
    // Không tạo dependency vòng tròn.
    // Không để service "chồng chất" logic không liên quan.

    public AuthServiceImpl(UserDao userDao, JwtService jwtServiceImpl) {
        this.userDao = userDao;
        this.jwtServiceImpl = jwtServiceImpl;
    }

    @Override
    public String authenticate(LoginRequest loginRequest) {
        User user = userDao.findUserByUsername(loginRequest.getUsername());

        if (user == null) {
            throw new NotFoundException("Invalid username");
        }

        if (!user.isActive()) {
            throw new ForbiddenException("User is inactive");
        }

        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return jwtServiceImpl.generateJwt(user);
    }
}