package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.exception.BadRequestException;
import com.ntn.culinary.exception.ForbiddenException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.User;
import com.ntn.culinary.request.LoginRequest;
import com.ntn.culinary.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtService jwtServiceImpl;

    @InjectMocks
    private AuthServiceImpl authService;

    // AUTHENTICATE - SUCCESS CASE
    @Test
    void authenticate_ShouldReturnJwt_WhenCredentialsAreValid() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String expectedJwt = "3m+zQh-/'chScZ?QF4C3dVym0sP=;O-d/\\\\,{3PIgH3]\\)y!vXL5Ti}YdK=\"Mzh."; // This is token for testing purposes

        LoginRequest loginRequest = new LoginRequest(username, password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(true);

        when(userDao.findUserByUsername(username)).thenReturn(user);
        when(jwtServiceImpl.generateJwt(user)).thenReturn(expectedJwt);

        // Act
        String result = authService.authenticate(loginRequest);

        // Assert
        assertEquals(expectedJwt, result);
        verify(userDao).findUserByUsername(username);
        verify(jwtServiceImpl).generateJwt(user);
    }

    // AUTHENTICATE - USER NOT FOUND
    @Test
    void authenticate_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        String username = "nonexistentuser";
        String password = "password123";
        LoginRequest loginRequest = new LoginRequest(username, password);

        when(userDao.findUserByUsername(username)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> authService.authenticate(loginRequest));

        assertEquals("Invalid username", exception.getMessage());
        verify(userDao).findUserByUsername(username);
        verify(jwtServiceImpl, never()).generateJwt(any());
    }

    // AUTHENTICATE - USER INACTIVE
    @Test
    void authenticate_ShouldThrowForbiddenException_WhenUserIsInactive() {
        // Arrange
        String username = "inactiveuser";
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        LoginRequest loginRequest = new LoginRequest(username, password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(false); // User is inactive

        when(userDao.findUserByUsername(username)).thenReturn(user);

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> authService.authenticate(loginRequest));

        assertEquals("User is inactive", exception.getMessage());
        verify(userDao).findUserByUsername(username);
        verify(jwtServiceImpl, never()).generateJwt(any());
    }

    // AUTHENTICATE - INVALID PASSWORD
    @Test
    void authenticate_ShouldThrowBadRequestException_WhenPasswordIsIncorrect() {
        // Arrange
        String username = "testuser";
        String correctPassword = "correctpassword";
        String incorrectPassword = "wrongpassword";
        String hashedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt());

        LoginRequest loginRequest = new LoginRequest(username, incorrectPassword);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(true);

        when(userDao.findUserByUsername(username)).thenReturn(user);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.authenticate(loginRequest));

        assertEquals("Invalid password", exception.getMessage());
        verify(userDao).findUserByUsername(username);
        verify(jwtServiceImpl, never()).generateJwt(any());
    }

    // AUTHENTICATE - BUSINESS VALIDATION: USERNAME CASE SENSITIVITY
    @Test
    void authenticate_ShouldBeConsistentWithUsernameCaseSensitivity() {
        // Arrange
        String username = "TestUser"; // Mixed case
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String expectedJwt = "3m+zQh-/'chScZ?QF4C3dVym0sP=;O-d/\\\\,{3PIgH3]\\)y!vXL5Ti}YdK=\"Mzh."; // This is token for testing purposes

        LoginRequest loginRequest = new LoginRequest(username, password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(true);

        when(userDao.findUserByUsername(username)).thenReturn(user);
        when(jwtServiceImpl.generateJwt(user)).thenReturn(expectedJwt);

        // Act
        String result = authService.authenticate(loginRequest);

        // Assert
        assertEquals(expectedJwt, result);
        verify(userDao).findUserByUsername(username); // Should search with exact case
    }

    // AUTHENTICATE - BUSINESS VALIDATION: JWT GENERATION FAILURE
    @Test
    void authenticate_ShouldPropagateException_WhenJwtGenerationFails() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        LoginRequest loginRequest = new LoginRequest(username, password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(true);

        when(userDao.findUserByUsername(username)).thenReturn(user);
        when(jwtServiceImpl.generateJwt(user)).thenThrow(new RuntimeException("JWT generation failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(loginRequest));

        assertEquals("JWT generation failed", exception.getMessage());
        verify(userDao).findUserByUsername(username);
        verify(jwtServiceImpl).generateJwt(user);
    }

    // AUTHENTICATE - BUSINESS VALIDATION: DATABASE ACCESS FAILURE
    @Test
    void authenticate_ShouldPropagateException_WhenDatabaseAccessFails() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        LoginRequest loginRequest = new LoginRequest(username, password);

        when(userDao.findUserByUsername(username)).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(loginRequest));

        assertEquals("Database connection failed", exception.getMessage());
        verify(userDao).findUserByUsername(username);
        verify(jwtServiceImpl, never()).generateJwt(any());
    }
}