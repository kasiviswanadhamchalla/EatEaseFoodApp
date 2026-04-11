package com.eatease.admin.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class PaymentServiceClientFallback implements PaymentServiceClient {

    @Override
    public List<Map<String, Object>> getAllPayments(String token, String roles) {
        return Collections.emptyList();
    }
}
