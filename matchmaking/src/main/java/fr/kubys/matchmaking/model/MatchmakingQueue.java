package fr.kubys.matchmaking.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MatchmakingQueue {
    private String waitingToken;
    private final Map<String, MatchResult> matches = new HashMap<>();

    public synchronized String join() {
        String token = UUID.randomUUID().toString();
        if (waitingToken != null) {
            MatchResult result = new MatchResult(waitingToken, token);
            matches.put(waitingToken, result);
            matches.put(token, result);
            waitingToken = null;
        } else {
            waitingToken = token;
        }
        return token;
    }

    public synchronized Optional<MatchResult> getMatch(String token) {
        return Optional.ofNullable(matches.get(token));
    }

    public synchronized boolean isWaiting(String token) {
        return token.equals(waitingToken);
    }

    public synchronized void cancel(String token) {
        if (token.equals(waitingToken)) {
            waitingToken = null;
        }
    }
}
