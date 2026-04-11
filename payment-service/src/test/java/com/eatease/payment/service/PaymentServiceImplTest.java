package com.eatease.payment.service;

import com.eatease.common.constants.PaymentStatus;
import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.payment.dto.PaymentRequest;
import com.eatease.payment.dto.PaymentResponse;
import com.eatease.payment.entity.Payment;
import com.eatease.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void processPaymentShouldThrowIfPaymentExists() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(10L);
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(new Payment()));

        assertThrows(BadRequestException.class, () -> paymentService.processPayment(5L, request));
    }

    @Test
    void getByIdShouldThrowIfNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getById(99L));
    }

    @Test
    void confirmPaymentShouldUpdateStatus() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(PaymentStatus.PENDING);
        
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        paymentService.confirmPayment(1L);

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        verify(paymentRepository).save(payment);
    }
}
