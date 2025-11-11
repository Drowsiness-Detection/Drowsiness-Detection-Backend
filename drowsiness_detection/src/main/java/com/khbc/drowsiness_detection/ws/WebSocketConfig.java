package com.khbc.drowsiness_detection.ws;

import com.khbc.drowsiness_detection.config.AppProps;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final DrowsySocketHandler handler;
    private final AppProps props;

    public WebSocketConfig(DrowsySocketHandler handler, AppProps props) {
        this.handler = handler;
        this.props = props;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, props.getWs().getPath())
                .setAllowedOrigins(props.getWs().getAllowedOrigins());
    }
}
