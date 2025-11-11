package com.khbc.drowsiness_detection.alert;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false, length = 16)
    private String state; // NORMAL | DROWSY | SLEEP

    @Column(nullable = false, length = 16)
    private String level; // INFO | WARN | CRITICAL

    @Column(columnDefinition = "text")
    private String message;

    @Column(columnDefinition = "jsonb")
    private String meta; // lưu JSON dạng String (MVP)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
