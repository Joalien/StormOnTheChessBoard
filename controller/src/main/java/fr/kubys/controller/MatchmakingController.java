package fr.kubys.controller;

import fr.kubys.matchmaking.MatchResult;
import fr.kubys.matchmaking.MatchmakingQueue;
import fr.kubys.repository.ChessBoardRepository;
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

    @Autowired
    public MatchmakingController(ChessBoardRepository chessBoardRepository) {
        this.chessBoardRepository = chessBoardRepository;
    }

    @PostMapping("/join")
    @CrossOrigin(origins = "*")
    public Map<String, String> join() {
        String token = queue.join();
        return Map.of("token", token);
    }

    @GetMapping("/status/{token}")
    @CrossOrigin(origins = "*")
    public Map<String, Object> status(@PathVariable String token) {
        Optional<MatchResult> match = queue.getMatch(token);
        if (match.isPresent()) {
            MatchResult result = match.get();
            synchronized (result) {
                if (result.getGameId() == null) {
                    result.setGameId(chessBoardRepository.createNewGame());
                }
            }
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
}
