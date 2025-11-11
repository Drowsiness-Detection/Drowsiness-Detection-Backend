package com.khbc.drowsiness_detection.alerts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;

public interface AlertRepo extends MongoRepository<AlertDoc, String> {
    Page<AlertDoc> findByDriverIdOrderByCreatedAtDesc(String driverId, Pageable p);

    Page<AlertDoc> findByCreatedAtBetween(Instant from, Instant to, Pageable p);

    Page<AlertDoc> findByStatusOrderByCreatedAtDesc(String status, Pageable p);
}
