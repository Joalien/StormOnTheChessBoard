package fr.kubys.matchmaking.controller;

import fr.kubys.matchmaking.model.MatchResult;
import fr.kubys.matchmaking.model.MatchmakingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/matchmaking")
public class MatchmakingController {

    private final MatchmakingQueue queue;
    private final RestTemplate restTemplate;
    private final MatchNotifier matchNotifier;
    private final StatsListener statsListener;
    private final String chessboardBaseUrl;

    @Autowired
    public MatchmakingController(MatchmakingQueue queue, RestTemplate restTemplate, MatchNotifier matchNotifier,
                                 StatsListener statsListener,
                                 @Value("${chessboard.base-url:http://localhost:9000}") String chessboardBaseUrl) {
        this.queue = queue;
        this.restTemplate = restTemplate;
        this.matchNotifier = matchNotifier;
        this.statsListener = statsListener;
        this.chessboardBaseUrl = chessboardBaseUrl;
    }

    @PostMapping("/join")
    public JoinResponse join() {
        String token = queue.join();
        Optional<MatchResult> match = queue.getMatch(token);
        if (match.isPresent()) {
            createGameAndNotify(match.get());
            MatchResult result = match.get();
            notifyStats();
            return new JoinResponse.Matched(token, result.getGameId(), result.getColorForToken(token));
        }
        notifyStats();
        return new JoinResponse.Waiting(token);
    }

    @GetMapping("/status/{token}")
    public MatchmakingStatus status(@PathVariable String token) {
        Optional<MatchResult> match = queue.getMatch(token);
        if (match.isPresent()) {
            MatchResult result = match.get();
            ensureGameCreated(result);
            return new MatchmakingStatus.Matched(result.getGameId(), result.getColorForToken(token));
        }
        if (queue.isWaiting(token)) {
            return new MatchmakingStatus.Waiting();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown token");
    }

    @DeleteMapping("/{token}")
    public void cancel(@PathVariable String token) {
        queue.cancel(token);
        notifyStats();
    }

    @GetMapping("/stats")
    public MatchmakingStats stats() {
        return new MatchmakingStats(queue.getWaitingCount(), queue.getActiveMatchCount());
    }

    public record MatchmakingStats(int waitingCount, int activeMatchCount) {}

    private void notifyStats() {
        statsListener.onStatsChanged(queue.getWaitingCount(), queue.getActiveMatchCount());
    }

    private void ensureGameCreated(MatchResult result) {
        synchronized (result) {
            if (result.getGameId() == null) {
                result.setGameId(restTemplate.postForObject(chessboardBaseUrl + "/api/chessboard", null, Integer.class));
            }
        }
    }

    private void createGameAndNotify(MatchResult result) {
        ensureGameCreated(result);
        int gameId = result.getGameId();
        matchNotifier.notifyMatch(result.getWhiteToken(),
                "{\"status\":\"matched\",\"gameId\":%d,\"color\":\"white\"}".formatted(gameId));
        matchNotifier.notifyMatch(result.getBlackToken(),
                "{\"status\":\"matched\",\"gameId\":%d,\"color\":\"black\"}".formatted(gameId));
    }
}
