package com.khbc.drowsiness_detection.infer;

import com.khbc.drowsiness_detection.config.AppProps;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

@Service
public class InferService {
    private final RestClient http;
    private final AppProps app;

    public InferService(AppProps app) {
        this.app = app;
        this.http = RestClient.builder()
                .baseUrl(app.getInfer().getBaseUrl())
                .build();
    }

    public record Result(double score, String state, String alertLevel, String message) {
    }

    @SuppressWarnings("unchecked")
    public Result infer(byte[] imageBytes) {
        String b64 = Base64.getEncoder().encodeToString(imageBytes);
        Map<String, Object> body = Map.of("imageBase64", b64);

        Map<String, Object> resp = http.post()
                .uri(app.getInfer().getEndpoint())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        double score = ((Number) resp.getOrDefault("score", 0.0)).doubleValue();
        String state = String.valueOf(resp.getOrDefault("state", "NORMAL"));
        Object lvl = resp.get("alertLevel");
        String alertLevel = (lvl == null) ? null : String.valueOf(lvl);
        String message = String.valueOf(resp.getOrDefault("message", ""));

        return new Result(score, state, alertLevel, message);
    }
}
