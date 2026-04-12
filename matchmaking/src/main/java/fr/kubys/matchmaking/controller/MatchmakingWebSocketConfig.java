package fr.kubys.matchmaking.controller;

import fr.kubys.matchmaking.model.MatchmakingQueue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MatchmakingWebSocketConfig {

    @Bean
    public MatchmakingQueue matchmakingQueue() {
        return new MatchmakingQueue();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
