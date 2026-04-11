package com.eatease.offer.controller;

import com.eatease.offer.dto.OfferRequest;
import com.eatease.offer.dto.OfferResponse;
import com.eatease.offer.service.OfferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferController controller;

    @Test
    void createShouldThrowIfNotAdmin() {
        OfferRequest request = new OfferRequest();
        assertThrows(RuntimeException.class, () -> controller.create(request, "CUSTOMER"));
    }

    @Test
    void createShouldReturnCreatedIfAdmin() {
        OfferRequest request = new OfferRequest();
        OfferResponse expected = new OfferResponse();
        when(offerService.create(request)).thenReturn(expected);

        ResponseEntity<OfferResponse> response = controller.create(request, "ADMIN,CUSTOMER");

        assertEquals(201, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getByIdShouldReturnOffer() {
        OfferResponse expected = new OfferResponse();
        when(offerService.getById(5L)).thenReturn(expected);

        OfferResponse response = controller.getById(5L);

        assertEquals(expected, response);
    }
}
