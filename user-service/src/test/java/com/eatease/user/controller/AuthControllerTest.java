package com.eatease.user.controller;

import com.eatease.user.dto.*;
import com.eatease.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenValidCredentials() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResponse expectedResponse = new AuthResponse("token123", "test@example.com", 1L,
                java.util.Arrays.asList("CUSTOMER"), "Test User", true);

        when(userService.login(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).login(request);
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenValidRequest() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password123",
                "Test User", "+1234567890", java.util.Arrays.asList("CUSTOMER"));
        AuthResponse expectedResponse = new AuthResponse("token123", "test@example.com", 1L,
                java.util.Arrays.asList("CUSTOMER"), "Test User", true);

        when(userService.register(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).register(request);
    }

    @Test
    void sendOtp_ShouldReturnOk_WhenValidEmail() {
        // Arrange
        SendOtpRequest request = new SendOtpRequest("test@example.com");

        // Act
        ResponseEntity<Void> response = authController.sendOtp(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).sendRegisterOtp("test@example.com");
    }

    @Test
    void verifyOtpAndRegister_ShouldReturnAuthResponse_WhenValidRequest() {
        // Arrange
        VerifyOtpRegisterRequest request = new VerifyOtpRegisterRequest("test@example.com", "123456",
                "password123", "Test User", "+1234567890", java.util.Arrays.asList("CUSTOMER"));
        AuthResponse expectedResponse = new AuthResponse("token123", "test@example.com", 1L,
                java.util.Arrays.asList("CUSTOMER"), "Test User", true);

        when(userService.verifyOtpAndRegister(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.verifyOtpAndRegister(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).verifyOtpAndRegister(request);
    }
}