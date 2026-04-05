package com.eatease.user.config;

import com.eatease.user.security.GatewayHeadersAuthenticationFilter;
import com.eatease.user.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final GatewayHeadersAuthenticationFilter gatewayHeadersAuthenticationFilter;

    @Value("${app.dev.allow-public-approve:false}")
    private boolean allowPublicApprove;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          GatewayHeadersAuthenticationFilter gatewayHeadersAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.gatewayHeadersAuthenticationFilter = gatewayHeadersAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/api/auth/login",
                            "/api/auth/register",
                            "/api/auth/register/send-otp",
                            "/api/auth/register/verify-otp"
                    ).permitAll();

                    if (allowPublicApprove) {
                        auth.requestMatchers("/api/users/pending-approval", "/api/users/*/approve", "/api/users/*/revoke-approval").permitAll();
                    }
                    auth.requestMatchers("/api/users/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(gatewayHeadersAuthenticationFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
