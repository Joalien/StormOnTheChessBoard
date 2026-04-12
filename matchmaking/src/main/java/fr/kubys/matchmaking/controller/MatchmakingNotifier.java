package fr.kubys.matchmaking.controller;

import fr.kubys.matchmaking.model.MatchmakingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class MatchmakingNotifier extends TextWebSocketHandler implements MatchNotifier {

    private static final Logger log = LoggerFactory.getLogger(MatchmakingNotifier.class);
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private MatchmakingQueue queue;

    public void setQueue(MatchmakingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String token = extractToken(session);
        if (token != null) {
            sessions.put(token, session);
            log.info("Matchmaking WS connected: token={}", token);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = extractToken(session);
        if (token != null) {
            sessions.remove(token);
            log.info("Matchmaking WS closed: token={}", token);
            if (queue != null) {
                queue.cancel(token);
            }
        }
    }

    @Override
    public void notifyMatch(String token, String jsonPayload) {
        WebSocketSession session = sessions.get(token);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(jsonPayload));
                log.info("Sent match notification to token={}", token);
            } catch (IOException e) {
                log.error("Failed to send match notification to token={}", token, e);
            }
        }
    }

    private String extractToken(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }
}
