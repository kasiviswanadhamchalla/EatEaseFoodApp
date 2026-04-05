package com.eatease.user.dto;

import com.eatease.common.constants.Role;

import java.time.Instant;
import java.util.Set;
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private Set<Role> roles;
    private boolean enabled;
    private boolean approved;
    private Instant createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String email, String name, String phone,
                        Set<Role> roles, boolean enabled,
                        boolean approved, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.roles = roles;
        this.enabled = enabled;
        this.approved = approved;
        this.createdAt = createdAt;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
