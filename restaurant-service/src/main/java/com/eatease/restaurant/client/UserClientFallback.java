package com.eatease.restaurant.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public Map<String, Object> getUserById(Long id) {
        return Collections.singletonMap("error", "User service unavailable");
    }
}
