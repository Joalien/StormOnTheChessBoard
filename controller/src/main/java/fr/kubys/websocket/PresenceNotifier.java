package fr.kubys.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PresenceNotifier extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(PresenceNotifier.class);
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("Presence connected: {} (total: {})", session.getRemoteAddress(), getConnectedCount());
        broadcastConnectedCount();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("Presence disconnected: {} (total: {})", session.getRemoteAddress(), getConnectedCount());
        broadcastConnectedCount();
    }

    public int getConnectedCount() {
        return sessions.size();
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
