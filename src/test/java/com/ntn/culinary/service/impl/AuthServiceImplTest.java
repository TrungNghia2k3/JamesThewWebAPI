package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtService jwtServiceImpl;

    @InjectMocks
    private AuthServiceImpl authService;

    // AUTHENTICATE
    @Test
    void authenticate_ShouldReturnJwt_WhenCredentialsAreValid() {
        // Arrange

        // Act

        // Assert
    }
}