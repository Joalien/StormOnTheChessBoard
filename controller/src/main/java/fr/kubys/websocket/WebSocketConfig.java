package fr.kubys.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public GameNotifier gameNotifier() {
        return new GameNotifier();
    }

    @Bean
    public PresenceNotifier presenceNotifier() {
        return new PresenceNotifier();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameNotifier(), "/ws/game/*").setAllowedOrigins("*");
        registry.addHandler(presenceNotifier(), "/ws/presence").setAllowedOrigins("*");
    }
}
