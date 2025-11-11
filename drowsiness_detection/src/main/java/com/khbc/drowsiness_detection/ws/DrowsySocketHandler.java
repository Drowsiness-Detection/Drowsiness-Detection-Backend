package com.khbc.drowsiness_detection.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khbc.drowsiness_detection.alert.Alert;
import com.khbc.drowsiness_detection.alert.AlertRepository;
import com.khbc.drowsiness_detection.infer.InferService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class DrowsySocketHandler extends TextWebSocketHandler {
    private final ObjectMapper om = new ObjectMapper();
    private final InferService inferService;
    private final AlertRepository alertRepo;
    private final MeterRegistry meter;

    public DrowsySocketHandler(InferService inferService, AlertRepository alertRepo, MeterRegistry meter) {
        this.inferService = inferService;
        this.alertRepo = alertRepo;
        this.meter = meter;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        meter.counter("ws_connections_opened").increment();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode root = om.readTree(message.getPayload());
            String type = root.path("type").asText("");
            JsonNode data = root.path("data");

            switch (type) {
                case "HELLO" -> {
                    String sessionId = data.path("sessionId").asText("sess");
                    session.getAttributes().put("sessionId", sessionId);
                    session.sendMessage(
                            json(Map.of("type", "ACK", "data", Map.of("hello", true, "sessionId", sessionId))));
                }
                case "CONTROL" -> {
                    session.sendMessage(json(Map.of("type", "ACK", "data", Map.of("control", true))));
                }
                case "FRAME" -> {
                    meter.counter("ws_frames_in_total").increment();
                    String b64 = data.path("imageBase64").asText("");
                    if (b64.isBlank()) {
                        session.sendMessage(json(Map.of("type", "ERROR", "data",
                                Map.of("code", "BAD_FRAME", "message", "Empty image"))));
                        return;
                    }
                    byte[] image = Base64.getDecoder().decode(b64.getBytes(StandardCharsets.UTF_8));
                    long t0 = System.currentTimeMillis();
                    InferService.Result r = inferService.infer(image);
                    long latency = System.currentTimeMillis() - t0;
                    meter.timer("ai_infer_latency_ms").record(latency, TimeUnit.MILLISECONDS);

                    session.sendMessage(json(Map.of("type", "RESULT", "data", Map.of(
                            "seq", data.path("seq").asInt(0),
                            "ts", Instant.now().toString(),
                            "score", r.score(),
                            "state", r.state(),
                            "latencyMs", latency))));

                    if (r.alertLevel() != null) {
                        Alert alert = Alert.builder()
                                .sessionId(String.valueOf(session.getAttributes().getOrDefault("sessionId", "sess")))
                                .score(r.score()).state(r.state()).level(r.alertLevel()).message(r.message())
                                .meta("{\"latencyMs\":" + latency + "}")
                                .build();
                        alertRepo.save(alert);
                        session.sendMessage(json(Map.of("type", "ALERT", "data", Map.of(
                                "sessionId", alert.getSessionId(),
                                "score", alert.getScore(),
                                "state", alert.getState(),
                                "level", alert.getLevel(),
                                "message", alert.getMessage(),
                                "ts", alert.getCreatedAt().toString()))));
                    }
                }
                default -> session.sendMessage(
                        json(Map.of("type", "ERROR", "data", Map.of("code", "UNKNOWN", "message", "Unknown type"))));
            }
        } catch (Exception e) {
            try {
                session.sendMessage(
                        json(Map.of("type", "ERROR", "data", Map.of("code", "EX", "message", e.getMessage()))));
            } catch (Exception ignored) {
            }
        }
    }

    private TextMessage json(Object o) throws Exception {
        return new TextMessage(om.writeValueAsString(o));
    }
}
