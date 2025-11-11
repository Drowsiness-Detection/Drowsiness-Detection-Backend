package com.khbc.drowsiness_detection.alerts;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepo repo;

    public AlertDoc saveAlert(String status, double confidence,
            Integer latencyMs, String imageUrl,
            String driverId, String tripId,
            Map<String, Object> aiPayload) {
        AlertDoc doc = new AlertDoc();
        doc.setStatus(status);
        doc.setConfidence(confidence);
        doc.setLatencyMs(latencyMs);
        doc.setImageUrl(imageUrl);
        doc.setDriverId(driverId);
        doc.setTripId(tripId);
        doc.setAi(aiPayload);
        return repo.save(doc);
    }

    public Page<AlertDoc> latest(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    public Page<AlertDoc> byDriver(String driverId, int page, int size) {
        return repo.findByDriverIdOrderByCreatedAtDesc(driverId, PageRequest.of(page, size));
    }

    public Page<AlertDoc> byRange(Instant from, Instant to, int page, int size) {
        return repo.findByCreatedAtBetween(from, to, PageRequest.of(page, size));
    }
}
