package fr.kubys.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameNotifier extends TextWebSocketHandler {

    private final ConcurrentHashMap<Integer, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer gameId = extractGameId(session);
        if (gameId != null) {
            sessions.computeIfAbsent(gameId, k -> new CopyOnWriteArraySet<>()).add(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer gameId = extractGameId(session);
        if (gameId != null) {
            Set<WebSocketSession> gameSessions = sessions.get(gameId);
            if (gameSessions != null) {
                gameSessions.remove(session);
                if (gameSessions.isEmpty()) sessions.remove(gameId);
            }
        }
    }

    public void notifyGame(Integer gameId) {
        Set<WebSocketSession> gameSessions = sessions.get(gameId);
        if (gameSessions == null) return;
        TextMessage message = new TextMessage("refresh");
        for (WebSocketSession session : gameSessions) {
            try {
                if (session.isOpen()) session.sendMessage(message);
            } catch (IOException ignored) {
            }
        }
    }

    private Integer extractGameId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
