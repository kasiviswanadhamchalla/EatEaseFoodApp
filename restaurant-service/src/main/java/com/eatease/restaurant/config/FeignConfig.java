package com.eatease.restaurant.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {

                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes == null) return;

                HttpServletRequest request = attributes.getRequest();

                String authHeader = request.getHeader("Authorization");
                String userId = request.getHeader("X-User-Id");
                String roles = request.getHeader("X-User-Roles");

                if (authHeader != null)
                    template.header("Authorization", authHeader);

                if (userId != null)
                    template.header("X-User-Id", userId);

                if (roles != null)
                    template.header("X-User-Roles", roles);
            }
        };
    }
}
