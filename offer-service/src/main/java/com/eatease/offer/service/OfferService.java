package com.eatease.offer.service;

import com.eatease.offer.dto.*;

import java.util.List;

public interface OfferService {
    OfferResponse create(OfferRequest request);
    OfferResponse getById(Long id);
    OfferResponse getByCode(String code);
    List<OfferResponse> findAll();
    List<OfferResponse> findActive();
    OfferResponse update(Long id, OfferRequest request);
    void delete(Long id);
    ApplyOfferResponse applyOffer(ApplyOfferRequest request);
    OfferResponse incrementUsage(String code);
}
