package com.eatease.offer.service;

import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.offer.dto.*;
import com.eatease.offer.entity.Offer;
import com.eatease.offer.repository.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository repo;

    public OfferServiceImpl(OfferRepository repo) {
        this.repo = repo;
    }

    // ---------------- CREATE ----------------
    @Override
    @Transactional
    public OfferResponse create(OfferRequest r) {

        if (repo.existsByCode(r.getCode().toUpperCase())) {
            throw new BadRequestException("Offer code already exists");
        }

        Offer o = new Offer();
        o.setCode(r.getCode().toUpperCase());
        o.setDescription(r.getDescription());
        o.setType(r.getType());
        o.setValue(r.getValue());
        o.setMinOrderAmount(r.getMinOrderAmount());
        o.setMaxDiscount(r.getMaxDiscount());
        o.setValidFrom(r.getValidFrom());
        o.setValidUntil(r.getValidUntil());
        o.setActive(r.getActive() != null ? r.getActive() : true);
        o.setUsageLimit(r.getUsageLimit());

        return toResponse(repo.save(o));
    }

    // ---------------- GET BY ID ----------------
    @Override
    public OfferResponse getById(Long id) {
        Offer o = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "id", id));
        return toResponse(o);
    }

    // ---------------- GET BY CODE ----------------
    @Override
    public OfferResponse getByCode(String code) {
        Offer o = repo.findByCodeAndActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "code", code));
        return toResponse(o);
    }

    // ---------------- FIND ALL ----------------
    @Override
    public List<OfferResponse> findAll() {
        List<OfferResponse> list = new ArrayList<>();
        for (Offer o : repo.findAll()) {
            list.add(toResponse(o));
        }
        return list;
    }

    // ---------------- FIND ACTIVE ----------------
    @Override
    public List<OfferResponse> findActive() {
        List<OfferResponse> list = new ArrayList<>();
        for (Offer o : repo.findByActiveTrue()) {
            list.add(toResponse(o));
        }
        return list;
    }

    // ---------------- UPDATE ----------------
    @Override
    @Transactional
    public OfferResponse update(Long id, OfferRequest r) {

        Offer o = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "id", id));

        if (r.getDescription() != null) o.setDescription(r.getDescription());
        if (r.getType() != null) o.setType(r.getType());
        if (r.getValue() != null) o.setValue(r.getValue());
        if (r.getMinOrderAmount() != null) o.setMinOrderAmount(r.getMinOrderAmount());
        if (r.getMaxDiscount() != null) o.setMaxDiscount(r.getMaxDiscount());
        if (r.getValidFrom() != null) o.setValidFrom(r.getValidFrom());
        if (r.getValidUntil() != null) o.setValidUntil(r.getValidUntil());
        if (r.getActive() != null) o.setActive(r.getActive());
        if (r.getUsageLimit() != null) o.setUsageLimit(r.getUsageLimit());

        return toResponse(repo.save(o));
    }

    // ---------------- DELETE ----------------
    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Offer", "id", id);
        }
        repo.deleteById(id);
    }

    // ---------------- APPLY OFFER ----------------
    @Override
    public ApplyOfferResponse applyOffer(ApplyOfferRequest r) {

        ApplyOfferResponse res = new ApplyOfferResponse();
        Offer o = repo.findByCodeAndActiveTrue(r.getCode().toUpperCase()).orElse(null);

        if (o == null) {
            res.setValid(false);
            res.setMessage("Invalid or inactive offer code");
            res.setFinalAmount(r.getOrderAmount());
            return res;
        }

        Instant now = Instant.now();

        if (o.getValidFrom() != null && now.isBefore(o.getValidFrom())) {
            res.setValid(false);
            res.setMessage("Offer not yet valid");
            res.setFinalAmount(r.getOrderAmount());
            return res;
        }

        if (o.getValidUntil() != null && now.isAfter(o.getValidUntil())) {
            res.setValid(false);
            res.setMessage("Offer has expired");
            res.setFinalAmount(r.getOrderAmount());
            return res;
        }

        if (o.getMinOrderAmount() != null &&
                r.getOrderAmount().compareTo(o.getMinOrderAmount()) < 0) {
            res.setValid(false);
            res.setMessage("Minimum order amount is " + o.getMinOrderAmount());
            res.setFinalAmount(r.getOrderAmount());
            return res;
        }

        if (o.getUsageLimit() != null && o.getUsedCount() >= o.getUsageLimit()) {
            res.setValid(false);
            res.setMessage("Offer usage limit reached");
            res.setFinalAmount(r.getOrderAmount());
            return res;
        }

        BigDecimal discount;

        if (o.getType() == Offer.OfferType.PERCENTAGE) {
            discount = r.getOrderAmount()
                    .multiply(o.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (o.getMaxDiscount() != null &&
                    discount.compareTo(o.getMaxDiscount()) > 0) {
                discount = o.getMaxDiscount();
            }
        } else {
            discount = o.getValue();
        }

        BigDecimal finalAmount =
                r.getOrderAmount().subtract(discount).max(BigDecimal.ZERO);

        res.setValid(true);
        res.setCode(o.getCode());
        res.setDiscountAmount(discount);
        res.setFinalAmount(finalAmount);
        res.setMessage("Offer applied");

        return res;
    }

    // ---------------- INCREMENT USAGE ----------------
    @Override
    @Transactional
    public OfferResponse incrementUsage(String code) {

        Offer o = repo.findByCodeAndActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Offer", "code", code));

        o.setUsedCount(o.getUsedCount() + 1);
        return toResponse(repo.save(o));
    }

    // ---------------- MAPPER ----------------
    private OfferResponse toResponse(Offer o) {

        OfferResponse r = new OfferResponse();
        r.setId(o.getId());
        r.setCode(o.getCode());
        r.setDescription(o.getDescription());
        r.setType(o.getType());
        r.setValue(o.getValue());
        r.setMinOrderAmount(o.getMinOrderAmount());
        r.setMaxDiscount(o.getMaxDiscount());
        r.setValidFrom(o.getValidFrom());
        r.setValidUntil(o.getValidUntil());
        r.setActive(o.isActive());
        r.setUsageLimit(o.getUsageLimit());
        r.setUsedCount(o.getUsedCount());
        r.setCreatedAt(o.getCreatedAt());

        return r;
    }
}
