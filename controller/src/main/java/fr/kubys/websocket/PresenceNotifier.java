package fr.kubys.websocket;

import fr.kubys.matchmaking.controller.StatsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PresenceNotifier extends TextWebSocketHandler implements StatsListener {

    private static final Logger log = LoggerFactory.getLogger(PresenceNotifier.class);
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("Presence connected: {} (total: {})", session.getRemoteAddress(), sessions.size());
        broadcast("{\"connected\":" + sessions.size() + "}");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("Presence disconnected: {} (total: {})", session.getRemoteAddress(), sessions.size());
        broadcast("{\"connected\":" + sessions.size() + "}");
    }

    @Override
    public void onStatsChanged(int waitingCount, int activeMatchCount) {
        broadcast("{\"waitingCount\":" + waitingCount + ",\"activeMatchCount\":" + activeMatchCount + "}");
    }

    private void broadcast(String json) {
        TextMessage msg = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            try {
                if (s.isOpen()) s.sendMessage(msg);
            } catch (IOException ignored) {}
        }
    }
}
