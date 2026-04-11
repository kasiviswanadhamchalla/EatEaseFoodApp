package com.eatease.user.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateWhenBearerTokenIsValid() throws Exception {
        JwtService jwtService = new JwtService("12345678901234567890123456789012", 60_000L);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        FilterChain filterChain = new MockFilterChain();
        String token = jwtService.generateToken("user@example.com", 7L, List.of("ADMIN"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(7L, request.getAttribute("userId"));
        assertEquals("user@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void shouldSkipWhenNoAuthorizationHeader() throws Exception {
        JwtService jwtService = new JwtService("12345678901234567890123456789012", 60_000L);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        FilterChain filterChain = new MockFilterChain();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
