package fr.kubys.websocket;

import fr.kubys.matchmaking.controller.MatchNotifier;
import fr.kubys.matchmaking.model.MatchmakingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PresenceNotifier extends TextWebSocketHandler implements MatchNotifier {

    private static final Logger log = LoggerFactory.getLogger(PresenceNotifier.class);
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, WebSocketSession> matchmakingSessions = new ConcurrentHashMap<>();
    private MatchmakingQueue queue;

    public void setQueue(MatchmakingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        broadcastConnectedCount();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        broadcastConnectedCount();
        // Cancel any matchmaking token associated with this session
        matchmakingSessions.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(session)) {
                log.info("Presence WS closed, cancelling matchmaking token={}", entry.getKey());
                if (queue != null) queue.cancel(entry.getKey());
                return true;
            }
            return false;
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload().trim();
        // Client sends matchmaking token to register for match notifications
        if (payload.startsWith("{")) {
            // Simple JSON parsing for {"matchmaking":"token"}
            int start = payload.indexOf("\"matchmaking\"");
            if (start >= 0) {
                int valueStart = payload.indexOf('"', payload.indexOf(':', start)) + 1;
                int valueEnd = payload.indexOf('"', valueStart);
                if (valueStart > 0 && valueEnd > valueStart) {
                    String token = payload.substring(valueStart, valueEnd);
                    matchmakingSessions.put(token, session);
                    log.info("Matchmaking registered on presence WS: token={}", token);
                }
            }
        }
    }

    @Override
    public void notifyMatch(String token, String jsonPayload) {
        WebSocketSession session = matchmakingSessions.get(token);
        log.info("notifyMatch token={} sessionFound={}", token, session != null);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(jsonPayload));
                log.info("Sent match notification to token={}", token);
            } catch (IOException e) {
                log.error("Failed to send match notification to token={}", token, e);
            }
        }
    }

    public int getConnectedCount() {
        return (int) sessions.stream().filter(WebSocketSession::isOpen).count();
    }

    private void broadcastConnectedCount() {
        TextMessage msg = new TextMessage("{\"connected\":" + getConnectedCount() + "}");
        for (WebSocketSession s : sessions) {
            try {
                if (s.isOpen()) s.sendMessage(msg);
            } catch (IOException ignored) {}
        }
    }
}
