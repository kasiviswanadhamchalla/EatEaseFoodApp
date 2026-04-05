package com.eatease.offer.controller;

import com.eatease.offer.dto.*;
import com.eatease.offer.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    private void checkAdmin(String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            throw new RuntimeException("Access Denied");
        }
    }

    @PostMapping
    public ResponseEntity<OfferResponse> create(
            @Valid @RequestBody OfferRequest request,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {

        checkAdmin(roles);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offerService.create(request));
    }

    @GetMapping("/{id}")
    public OfferResponse getById(@PathVariable Long id) {
        return offerService.getById(id);
    }

    @GetMapping("/code/{code}")
    public OfferResponse getByCode(@PathVariable String code) {
        return offerService.getByCode(code);
    }

    @GetMapping
    public List<OfferResponse> findAll(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        return activeOnly ? offerService.findActive()
                : offerService.findAll();
    }

    @PutMapping("/{id}")
    public OfferResponse update(
            @PathVariable Long id,
            @Valid @RequestBody OfferRequest request,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {

        checkAdmin(roles);
        return offerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {

        checkAdmin(roles);
        offerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/apply")
    public ApplyOfferResponse apply(@Valid @RequestBody ApplyOfferRequest request) {
        return offerService.applyOffer(request);
    }
}
