package com.eatease.offer.service;

import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.offer.dto.ApplyOfferRequest;
import com.eatease.offer.dto.ApplyOfferResponse;
import com.eatease.offer.dto.OfferRequest;
import com.eatease.offer.entity.Offer;
import com.eatease.offer.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    @Test
    void createShouldThrowIfCodeExists() {
        OfferRequest request = new OfferRequest();
        request.setCode("SUMMER50");
        when(offerRepository.existsByCode("SUMMER50")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> offerService.create(request));
    }

    @Test
    void applyOfferShouldReturnInvalidIfNotFound() {
        ApplyOfferRequest request = new ApplyOfferRequest();
        request.setCode("INVALID");
        request.setOrderAmount(BigDecimal.valueOf(100));

        when(offerRepository.findByCodeAndActiveTrue("INVALID")).thenReturn(Optional.empty());

        ApplyOfferResponse response = offerService.applyOffer(request);

        assertFalse(response.isValid());
        assertEquals(BigDecimal.valueOf(100), response.getFinalAmount());
    }

    @Test
    void applyOfferShouldApplyFlatDiscount() {
        ApplyOfferRequest request = new ApplyOfferRequest();
        request.setCode("FLAT10");
        request.setOrderAmount(BigDecimal.valueOf(100));

        Offer offer = new Offer();
        offer.setCode("FLAT10");
        offer.setActive(true);
        offer.setType(Offer.OfferType.FIXED_AMOUNT);
        offer.setValue(BigDecimal.valueOf(10));
        
        when(offerRepository.findByCodeAndActiveTrue("FLAT10")).thenReturn(Optional.of(offer));

        ApplyOfferResponse response = offerService.applyOffer(request);

        assertTrue(response.isValid());
        assertEquals(0, BigDecimal.valueOf(90).compareTo(response.getFinalAmount()));
    }
}
