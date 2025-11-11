package com.khbc.drowsiness_detection.alerts;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "alerts")
public class AlertDoc {
    @Id
    private String id;

    private String driverId;
    private String tripId;

    private String status; // "awake" | "drowsy"
    private Double confidence; // 0..1

    private Integer latencyMs;
    private String imageUrl;
    private Map<String, Object> ai; // payload từ FastAPI

    private Instant createdAt = Instant.now();
}
