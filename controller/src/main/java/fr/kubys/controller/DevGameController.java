package fr.kubys.controller;

import fr.kubys.command.ReplaceLastCardCommand;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.websocket.GameNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dev")
@ConditionalOnProperty(name = "dev.tools.enabled", havingValue = "true")
public class DevGameController {

    private final ChessBoardRepository chessBoardRepository;
    private final GameNotifier gameNotifier;

    public DevGameController(ChessBoardRepository chessBoardRepository, GameNotifier gameNotifier) {
        this.chessBoardRepository = chessBoardRepository;
        this.gameNotifier = gameNotifier;
    }

    @PostMapping("/{gameId}/hand/replace-last")
    public ResponseEntity<Void> replaceLastCard(
            @PathVariable Integer gameId,
            @RequestBody Map<String, String> body) {
        chessBoardRepository.saveCommand(ReplaceLastCardCommand.builder()
                .gameId(gameId)
                .cardClassName(body.get("cardName"))
                .build());
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }
}
