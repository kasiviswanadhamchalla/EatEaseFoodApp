package com.eatease.user.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    @Value("${app.jwt.secret}")
    private String secret;

    @Mock
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize JwtService with test values
        jwtService = new JwtService("test-secret-key-for-testing-only-1234567890", 3600000L); // 1 hour
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        // Arrange
        String email = "test@example.com";
        Long userId = 1L;
        List<String> roles = Arrays.asList("CUSTOMER", "ADMIN");

        // Act
        String token = jwtService.generateToken(email, userId, roles);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token can be parsed
        Claims claims = jwtService.parseToken(token);
        assertEquals(email, claims.getSubject());
        assertEquals(userId, claims.get("userId"));
        assertEquals(roles, claims.get("roles"));
    }

    @Test
    void getEmail_ShouldReturnEmailFromToken() {
        // Arrange
        String email = "test@example.com";
        Long userId = 1L;
        List<String> roles = Arrays.asList("CUSTOMER");
        String token = jwtService.generateToken(email, userId, roles);

        // Act
        String extractedEmail = jwtService.getEmail(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void getUserId_ShouldReturnUserIdFromToken() {
        // Arrange
        String email = "test@example.com";
        Long userId = 1L;
        List<String> roles = Arrays.asList("CUSTOMER");
        String token = jwtService.generateToken(email, userId, roles);

        // Act
        Long extractedUserId = jwtService.getUserId(token);

        // Assert
        assertEquals(userId, extractedUserId);
    }

    @Test
    void getRoles_ShouldReturnRolesFromToken() {
        // Arrange
        String email = "test@example.com";
        Long userId = 1L;
        List<String> roles = Arrays.asList("CUSTOMER", "ADMIN");
        String token = jwtService.generateToken(email, userId, roles);

        // Act
        List<String> extractedRoles = jwtService.getRoles(token);

        // Assert
        assertEquals(roles, extractedRoles);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        // Arrange
        String email = "test@example.com";
        Long userId = 1L;
        List<String> roles = Arrays.asList("CUSTOMER");
        String token = jwtService.generateToken(email, userId, roles);

        // Act
        boolean isValid = jwtService.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtService.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        // Arrange - create token with very short expiration
        JwtService shortExpirationJwtService = new JwtService(
                "test-secret-key-for-testing-only-1234567890", 100L); // 100ms

        String email = "test@example.com";
        Long userId = 1L;
        List<String> roles = Arrays.asList("CUSTOMER");
        String token = shortExpirationJwtService.generateToken(email, userId, roles);

        // Wait for token to expire
        Thread.sleep(150);

        // Act
        boolean isValid = shortExpirationJwtService.validateToken(token);

        // Assert
        assertFalse(isValid);
    }
}