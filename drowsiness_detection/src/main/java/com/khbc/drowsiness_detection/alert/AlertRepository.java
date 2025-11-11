package com.khbc.drowsiness_detection.alert;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    Page<Alert> findBySessionIdAndCreatedAtBetween(
            String sessionId, Instant from, Instant to, Pageable pageable);

    Page<Alert> findByCreatedAtBetween(
            Instant from, Instant to, Pageable pageable);
}
