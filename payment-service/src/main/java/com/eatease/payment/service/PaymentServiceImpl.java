package com.eatease.payment.service;

import com.eatease.common.constants.PaymentStatus;
import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.payment.dto.PaymentRequest;
import com.eatease.payment.dto.PaymentResponse;
import com.eatease.payment.entity.Payment;
import com.eatease.payment.repository.PaymentRepository;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(Long customerId, PaymentRequest request) {

        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new BadRequestException("Payment already exists for this order");
        }

        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(
                                    request.getAmount()
                                            .multiply(BigDecimal.valueOf(100))
                                            .longValueExact()
                            )
                            .setCurrency("inr")
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods
                                            .builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setCustomerId(customerId);
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod("STRIPE");
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionId(paymentIntent.getId());

            payment = paymentRepository.save(payment);

            PaymentResponse response = toResponse(payment);
            response.setClientSecret(paymentIntent.getClientSecret());

            return response;

        } catch (Exception e) {
            throw new BadRequestException("Stripe payment failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void confirmPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }

    @Override
    public PaymentResponse getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return toResponse(payment);
    }

    @Override
    public PaymentResponse getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setOrderId(p.getOrderId());
        r.setCustomerId(p.getCustomerId());
        r.setAmount(p.getAmount());
        r.setStatus(p.getStatus());
        r.setTransactionId(p.getTransactionId());
        r.setPaymentMethod(p.getPaymentMethod());
        r.setCreatedAt(p.getCreatedAt());
        r.setFailureReason(p.getFailureReason());
        return r;
    }
}
