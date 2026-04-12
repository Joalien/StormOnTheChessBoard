package fr.kubys.matchmaking.controller;

import fr.kubys.matchmaking.model.MatchmakingQueue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class MatchmakingWebSocketConfig implements WebSocketConfigurer {

    @Bean
    public MatchmakingQueue matchmakingQueue() {
        return new MatchmakingQueue();
    }

    @Bean
    public MatchmakingNotifier matchmakingNotifier(MatchmakingQueue queue) {
        MatchmakingNotifier notifier = new MatchmakingNotifier();
        notifier.setQueue(queue);
        return notifier;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(matchmakingNotifier(null), "/ws/matchmaking/*").setAllowedOrigins("*");
    }
}
