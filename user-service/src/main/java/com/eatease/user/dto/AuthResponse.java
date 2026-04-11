package com.eatease.user.dto;

import java.util.List;
public class AuthResponse {

    private String token;
    private String email;
    private Long userId;
    private List<String> roles;
    private String name;
    private boolean approved;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String email, Long userId,
                        List<String> roles, String name, boolean approved) {
        this.token = token;
        this.email = email;
        this.userId = userId;
        this.roles = roles;
        this.name = name;
        this.approved = approved;
    }

    public AuthResponse(String token, String email, Long userId,
                        List<String> roles, String name, boolean approved, String message) {
        this.token = token;
        this.email = email;
        this.userId = userId;
        this.roles = roles;
        this.name = name;
        this.approved = approved;
        this.message = message;
    }

    // getters & setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getMessage() {
        return message;
    }
}
