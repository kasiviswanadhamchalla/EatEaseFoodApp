package com.eatease.payment.controller;

import com.eatease.common.constants.PaymentStatus;
import com.eatease.payment.dto.PaymentRequest;
import com.eatease.payment.dto.PaymentResponse;
import com.eatease.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController controller;

    @Test
    void processPaymentShouldReturnCreated() {
        PaymentRequest request = new PaymentRequest();
        PaymentResponse expected = new PaymentResponse();
        when(paymentService.processPayment(10L, request)).thenReturn(expected);

        ResponseEntity<PaymentResponse> response = controller.processPayment(10L, request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getByIdShouldReturnOk() {
        PaymentResponse expected = new PaymentResponse();
        when(paymentService.getById(5L)).thenReturn(expected);

        ResponseEntity<PaymentResponse> response = controller.getById(5L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getByStatusShouldReturnList() {
        List<PaymentResponse> expected = List.of(new PaymentResponse());
        when(paymentService.getByStatus(PaymentStatus.SUCCESS)).thenReturn(expected);

        ResponseEntity<List<PaymentResponse>> response = controller.getByStatus(PaymentStatus.SUCCESS);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }
}
