package com.eatease.offer.repository;

import com.eatease.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByCodeAndActiveTrue(String code);

    List<Offer> findByActiveTrue();

    List<Offer> findByActiveTrueAndValidFromBeforeAndValidUntilAfter(Instant now1, Instant now2);

    boolean existsByCode(String code);
}
