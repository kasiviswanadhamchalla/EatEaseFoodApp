package com.eatease.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users")
    List<Map<String, Object>> getAllUsers(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Roles") String roles
    );

    @PatchMapping("/api/users/{id}/approve")
    Map<String, Object> approveUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Roles") String roles
    );

    @DeleteMapping("/api/users/{id}")
    void deleteUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Roles") String roles
    );
}
