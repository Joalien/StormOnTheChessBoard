package fr.kubys.controller;

import fr.kubys.ai.*;
import fr.kubys.card.params.CardParam;
import fr.kubys.command.*;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.piece.PromotionPiece;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.GamePresets;
import fr.kubys.websocket.GameNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.kubys.mapper.OutputMapper.mapToDto;

@RestController
@RequestMapping("/api/chessboard")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    ChessBoardRepository chessBoardRepository;
    GameNotifier gameNotifier;
    AiGameService aiGameService;
    AiStrategy defaultStrategy;
    String stockfishPathConfig;

    @Autowired
    public GameController(ChessBoardRepository chessBoardRepository, GameNotifier gameNotifier, AiGameService aiGameService,
                          @Value("${stockfish.path:}") String stockfishPath) {
        this.chessBoardRepository = chessBoardRepository;
        this.gameNotifier = gameNotifier;
        this.aiGameService = aiGameService;
        this.stockfishPathConfig = stockfishPath;
        this.defaultStrategy = createDefaultStrategy(stockfishPath);
        createInitialState(); // FIXME remove me later on
    }

    private AiStrategy createDefaultStrategy(String stockfishPath) {
        log.info("Using CardAwareStrategy");
        return new CardAwareStrategy(chessBoardRepository);
    }

    private AiStrategy createStockfishStrategy(String stockfishPath) {
        String resolvedPath = resolveStockfishPath(stockfishPath);
        if (resolvedPath != null) {
            log.info("Using Fairy-Stockfish at {} with MaterialStrategy fallback", resolvedPath);
            return new StockfishStrategy(resolvedPath, new MaterialStrategy());
        }
        log.info("Stockfish binary not found, falling back to MaterialStrategy");
        return new MaterialStrategy();
    }

    private AiStrategy resolveStrategy(String name) {
        if (name == null) return defaultStrategy;
        return switch (name.toLowerCase()) {
            case "random" -> new RandomMoveStrategy();
            case "material" -> new MaterialStrategy();
            case "cards" -> new CardAwareStrategy(chessBoardRepository);
            case "stockfish" -> createStockfishStrategy(stockfishPathConfig);
            default -> defaultStrategy;
        };
    }

    private String resolveStockfishPath(String configuredPath) {
        if (configuredPath != null && !configuredPath.isBlank() && new java.io.File(configuredPath).canExecute()) {
            return configuredPath;
        }
        try {
            var resource = getClass().getClassLoader().getResource("bin/fairy-stockfish");
            if (resource == null) return null;

            if ("file".equals(resource.getProtocol())) {
                java.io.File file = new java.io.File(resource.toURI());
                if (file.canExecute()) return file.getAbsolutePath();
                if (file.setExecutable(true) && file.canExecute()) return file.getAbsolutePath();
                return null;
            }

            // Resource is inside a jar (typical for spring-boot:run / packaged jar).
            // Extract it to a temp file so it can be executed.
            java.io.File tmp = java.io.File.createTempFile("fairy-stockfish-", "");
            tmp.deleteOnExit();
            try (var in = resource.openStream()) {
                java.nio.file.Files.copy(in, tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            if (tmp.setExecutable(true) && tmp.canExecute()) {
                log.info("Extracted Fairy-Stockfish from classpath to {}", tmp.getAbsolutePath());
                return tmp.getAbsolutePath();
            }
        } catch (Exception e) {
            log.warn("Could not resolve classpath stockfish: {}", e.getMessage());
        }
        return null;
    }

    private void createInitialState() {
        GamePresets.createKingsWithPawnsGame(chessBoardRepository); // game 1
        GamePresets.createSicilianGame(chessBoardRepository);       // game 2
        GamePresets.createEffectShowcaseGame(chessBoardRepository); // game 3
    }

    @GetMapping
    public Set<Integer> listGames() {
        return chessBoardRepository.getGameIds();
    }

    @PostMapping
    public ResponseEntity<Integer> startGame() {
        return new ResponseEntity<>(chessBoardRepository.createNewGame(), HttpStatus.CREATED);
    }

    @PostMapping("/ai")
    public ResponseEntity<Map<String, Object>> startAiGame(@RequestParam(required = false) String strategy) {
        Integer gameId = chessBoardRepository.createNewGame();
        AiStrategy resolved = resolveStrategy(strategy);
        log.info("[AI Game {}] Starting with requested strategy={} → {}", gameId, strategy, resolved.getClass().getSimpleName());
        aiGameService.registerAiGame(gameId, Color.BLACK, resolved);
        return new ResponseEntity<>(Map.of("gameId", gameId, "color", "white"), HttpStatus.CREATED);
    }

    @PostMapping("/{gameId}/endTurn")
    public ResponseEntity<Integer> endTurn(@PathVariable Integer gameId) {
        EndTurnCommand endTurnCommand = EndTurnCommand.builder().gameId(gameId).build();
        chessBoardRepository.saveCommand(endTurnCommand);
        gameNotifier.notifyGame(gameId);
        aiGameService.schedulePlayIfAiTurn(gameId, () -> gameNotifier.notifyGame(gameId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/undo")
    public ResponseEntity<Integer> undo(@PathVariable Integer gameId) {
        chessBoardRepository.undoLastCommand(gameId);
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}")
    public ChessBoardDto getGameById(@PathVariable Integer gameId) {
        return mapToDto(gameId, chessBoardRepository.getChessBoardService(gameId));
    }

    @PostMapping("/{gameId}/card/{cardName}")
    public <T extends CardParam> ResponseEntity<Void> updateGame(@PathVariable Integer gameId, @PathVariable String cardName, @RequestBody Map<String, Object> param) {
        PlayCardWithImmutableParamCommand<T> command = PlayCardWithImmutableParamCommand.<T>builder()
                .gameId(gameId)
                .cardName(cardName)
                .param(param)
                .build();
        chessBoardRepository.saveCommand(command);
        gameNotifier.notifyGame(gameId);
        aiGameService.schedulePlayIfAiTurn(gameId, () -> gameNotifier.notifyGame(gameId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/promote/{position}/{piece}")
    public ResponseEntity<Void> promote(@PathVariable Integer gameId, @PathVariable String position, @PathVariable PromotionPiece piece) {
        chessBoardRepository.saveCommand(
            PromoteCommand.builder()
                .gameId(gameId)
                .position(Position.valueOf(position))
                .piece(piece)
                .build()
        );
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}/legalMoves/{position}")
    public Set<String> getLegalMoves(@PathVariable Integer gameId, @PathVariable String position) {
        return chessBoardRepository.getChessBoardService(gameId)
                .getLegalMoves(Position.valueOf(position))
                .stream()
                .map(Position::name)
                .collect(Collectors.toSet());
    }

    @PostMapping("/{gameId}/shuffle")
    public ResponseEntity<Void> shuffle(@PathVariable Integer gameId) {
        ShuffleCommand shuffleCommand = ShuffleCommand.builder().gameId(gameId).build();
        chessBoardRepository.saveCommand(shuffleCommand);
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/move/{from}/to/{to}")
    public ResponseEntity<Void> move(@PathVariable Integer gameId, @PathVariable String from, @PathVariable String to) {
        Command command = PlayMoveCommand.builder()
                .gameId(gameId)
                .from(Position.valueOf(from))
                .to(Position.valueOf(to))
                .build();
        chessBoardRepository.saveCommand(command);
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }
}