package com.eatease.admin.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public List<Map<String, Object>> getAllUsers(String token, String roles) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> approveUser(Long id, String token, String roles) {
        return Collections.singletonMap("status", "error - user service unavailable");
    }

    @Override
    public void deleteUser(Long id, String token, String roles) {
        // Log the failure in a real app, do nothing here
    }
}
