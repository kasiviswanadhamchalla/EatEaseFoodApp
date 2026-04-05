package com.eatease.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.core.Ordered;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * When the API Gateway forwards a request after validating the JWT, it adds
 * X-User-Id and X-User-Roles. If Authorization is stripped by the gateway,
 * we can still authenticate using these headers so admin and other protected
 * endpoints work.
 */
@Component
public class GatewayHeadersAuthenticationFilter extends OncePerRequestFilter implements Ordered {

    private static final int ORDER = -3;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Only use headers when there is no Bearer token (JWT filter will handle that)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userIdHeader = request.getHeader("X-User-Id");
        String rolesHeader = request.getHeader("X-User-Roles");
        if (userIdHeader != null && !userIdHeader.isBlank() && rolesHeader != null && !rolesHeader.isBlank()) {
            try {
                Long userId = Long.parseLong(userIdHeader.trim());
                List<String> roles = Arrays.stream(rolesHeader.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                if (!roles.isEmpty()) {
                    UserPrincipal principal = new UserPrincipal(userId, "user-" + userId + "@gateway", roles);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    request.setAttribute("userId", userId);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (NumberFormatException ignored) {
                // Invalid X-User-Id, skip
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
