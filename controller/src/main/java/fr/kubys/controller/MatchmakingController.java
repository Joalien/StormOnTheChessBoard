package fr.kubys.controller;

import fr.kubys.matchmaking.MatchResult;
import fr.kubys.matchmaking.MatchmakingQueue;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.websocket.MatchmakingNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/matchmaking")
public class MatchmakingController {

    private final MatchmakingQueue queue = new MatchmakingQueue();
    private final ChessBoardRepository chessBoardRepository;
    private final MatchmakingNotifier matchmakingNotifier;

    @Autowired
    public MatchmakingController(ChessBoardRepository chessBoardRepository, MatchmakingNotifier matchmakingNotifier) {
        this.chessBoardRepository = chessBoardRepository;
        this.matchmakingNotifier = matchmakingNotifier;
    }

    @PostMapping("/join")
    @CrossOrigin(origins = "*")
    public Map<String, String> join() {
        String token = queue.join();
        queue.getMatch(token).ifPresent(this::createGameAndNotify);
        return Map.of("token", token);
    }

    @GetMapping("/status/{token}")
    @CrossOrigin(origins = "*")
    public Map<String, Object> status(@PathVariable String token) {
        Optional<MatchResult> match = queue.getMatch(token);
        if (match.isPresent()) {
            MatchResult result = match.get();
            ensureGameCreated(result);
            return Map.of(
                    "status", "matched",
                    "gameId", result.getGameId(),
                    "color", result.getColorForToken(token)
            );
        }
        if (queue.isWaiting(token)) {
            return Map.of("status", "waiting");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown token");
    }

    @DeleteMapping("/{token}")
    @CrossOrigin(origins = "*")
    public void cancel(@PathVariable String token) {
        queue.cancel(token);
    }

    private void ensureGameCreated(MatchResult result) {
        synchronized (result) {
            if (result.getGameId() == null) {
                result.setGameId(chessBoardRepository.createNewGame());
            }
        }
    }

    private void createGameAndNotify(MatchResult result) {
        ensureGameCreated(result);
        int gameId = result.getGameId();
        matchmakingNotifier.notifyMatch(result.getWhiteToken(),
                "{\"status\":\"matched\",\"gameId\":%d,\"color\":\"white\"}".formatted(gameId));
        matchmakingNotifier.notifyMatch(result.getBlackToken(),
                "{\"status\":\"matched\",\"gameId\":%d,\"color\":\"black\"}".formatted(gameId));
    }
}
