package fr.kubys.matchmaking.controller;

/**
 * Interface for notifying players when a match is found.
 * Implemented by the WebSocket handler in the controller module.
 */
public interface MatchNotifier {
    void notifyMatch(String token, String jsonPayload);
}
