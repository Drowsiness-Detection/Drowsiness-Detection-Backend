
package com.khbc.drowsiness_detection.config;

import jakarta.validation.constraints.*; // dùng cho @Validated
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProps {

    public static class Infer {
        @NotBlank
        private String baseUrl;
        @NotBlank
        private String endpoint = "/infer/drowsiness";
        @Min(500)
        @Max(10000)
        private int timeoutMs = 3000;

        // getters/setters
        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }

    public static class Ws {
        @NotBlank
        private String path = "/ws/drowsiness";
        @NotBlank
        private String allowedOrigins = "*";
        @Min(4096)
        private int maxTextBytes = 262_144;
        @Min(4096)
        private int maxBinaryBytes = 1_048_576;
        @Min(1000)
        private int pingIntervalMs = 30_000;

        // getters/setters
        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public int getMaxTextBytes() {
            return maxTextBytes;
        }

        public void setMaxTextBytes(int maxTextBytes) {
            this.maxTextBytes = maxTextBytes;
        }

        public int getMaxBinaryBytes() {
            return maxBinaryBytes;
        }

        public void setMaxBinaryBytes(int maxBinaryBytes) {
            this.maxBinaryBytes = maxBinaryBytes;
        }

        public int getPingIntervalMs() {
            return pingIntervalMs;
        }

        public void setPingIntervalMs(int pingIntervalMs) {
            this.pingIntervalMs = pingIntervalMs;
        }
    }

    private Infer infer = new Infer();
    private Ws ws = new Ws();

    public Infer getInfer() {
        return infer;
    }

    public void setInfer(Infer infer) {
        this.infer = infer;
    }

    public Ws getWs() {
        return ws;
    }

    public void setWs(Ws ws) {
        this.ws = ws;
    }
}
