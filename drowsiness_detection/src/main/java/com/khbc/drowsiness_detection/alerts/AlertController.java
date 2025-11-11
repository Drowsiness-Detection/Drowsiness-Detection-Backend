package com.khbc.drowsiness_detection.alerts;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;

    @PostMapping
    public AlertDoc create(@RequestBody Map<String, Object> body) {
        String status = (String) body.getOrDefault("status", "drowsy");
        double confidence = ((Number) body.getOrDefault("confidence", 0.8)).doubleValue();
        Integer latencyMs = body.get("latencyMs") == null ? null : ((Number) body.get("latencyMs")).intValue();
        String imageUrl = (String) body.get("imageUrl");
        String driverId = (String) body.get("driverId");
        String tripId = (String) body.get("tripId");
        Map<String, Object> ai = (Map<String, Object>) body.get("ai");
        return service.saveAlert(status, confidence, latencyMs, imageUrl, driverId, tripId, ai);
    }

    @GetMapping
    public Page<AlertDoc> list(
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size) {
        if (driverId != null && !driverId.isBlank())
            return service.byDriver(driverId, page, size);
        if (from != null && to != null)
            return service.byRange(from, to, page, size);
        return service.latest(page, size);
    }
}
