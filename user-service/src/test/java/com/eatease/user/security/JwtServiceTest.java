package com.eatease.user.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("12345678901234567890123456789012", 60_000L);
    }

    @Test
    void generateAndParseTokenShouldContainExpectedClaims() {
        String token = jwtService.generateToken("user@example.com", 10L, List.of("ADMIN", "CUSTOMER"));

        Claims claims = jwtService.parseToken(token);

        assertEquals("user@example.com", claims.getSubject());
        assertEquals(10L, jwtService.getUserId(token));
        assertEquals(List.of("ADMIN", "CUSTOMER"), jwtService.getRoles(token));
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateTokenShouldReturnFalseForInvalidToken() {
        assertFalse(jwtService.validateToken("not-a-jwt-token"));
    }
}
