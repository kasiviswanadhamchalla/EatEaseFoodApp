package com.eatease.payment.service;

import com.eatease.common.constants.PaymentStatus;
import com.eatease.payment.dto.PaymentRequest;
import com.eatease.payment.dto.PaymentResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(Long customerId, PaymentRequest request);

    void confirmPayment(Long paymentId);

    PaymentResponse getById(Long id);
    PaymentResponse getByOrderId(Long orderId);
    List<PaymentResponse> getByCustomerId(Long customerId);
    List<PaymentResponse> getByStatus(PaymentStatus status);
    List<PaymentResponse> getAllPayments();
}
