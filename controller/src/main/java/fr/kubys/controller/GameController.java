package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.card.params.CardParam;
import fr.kubys.command.*;
import fr.kubys.core.Position;
import fr.kubys.piece.PromotionPiece;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.mapper.MappingException;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.GamePresets;
import fr.kubys.websocket.GameNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.kubys.mapper.OutputMapper.mapToDto;

@RestController
@RequestMapping("/chessboard")
public class GameController {

    ChessBoardRepository chessBoardRepository;
    GameNotifier gameNotifier;

    @Autowired
    public GameController(ChessBoardRepository chessBoardRepository, GameNotifier gameNotifier) {
        this.chessBoardRepository = chessBoardRepository;
        this.gameNotifier = gameNotifier;
        createInitialState(); // FIXME remove me later on
    }

    private void createInitialState() {
        GamePresets.createKingsWithPawnsGame(chessBoardRepository); // game 1
        GamePresets.createSicilianGame(chessBoardRepository);       // game 2
    }

    // FIXME split into smaller endpoints in order to allow front to fetch in multiple requests
    @PostMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<Integer> startGame() {
        return new ResponseEntity<>(chessBoardRepository.createNewGame(), HttpStatus.CREATED);
    }

    @PostMapping("/{gameId}/endTurn")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Integer> endTurn(@PathVariable Integer gameId) {
        EndTurnCommand endTurnCommand = EndTurnCommand.builder().gameId(gameId).build();
        chessBoardRepository.saveCommand(endTurnCommand);
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/undo")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Integer> undo(@PathVariable Integer gameId) {
        chessBoardRepository.undoLastCommand(gameId);
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}")
    @CrossOrigin(origins = "*")
    public ChessBoardDto getGameById(@PathVariable Integer gameId) {
        return mapToDto(gameId, chessBoardRepository.getChessBoardService(gameId));
    }

    @PostMapping("/{gameId}/card/{cardName}")
    @CrossOrigin(origins = "*")
    public <T extends CardParam> ResponseEntity<Void> updateGame(@PathVariable Integer gameId, @PathVariable String cardName, @RequestBody Map<String, Object> param) {
        PlayCardWithImmutableParamCommand<T> command = PlayCardWithImmutableParamCommand.<T>builder()
                .gameId(gameId)
                .cardName(cardName)
                .param(param)
                .build();
        chessBoardRepository.saveCommand(command);
        gameNotifier.notifyGame(gameId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/promote/{position}/{piece}")
    @CrossOrigin(origins = "*")
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
    @CrossOrigin(origins = "*")
    public Set<String> getLegalMoves(@PathVariable Integer gameId, @PathVariable String position) {
        return chessBoardRepository.getChessBoardService(gameId)
                .getLegalMoves(Position.valueOf(position))
                .stream()
                .map(Position::name)
                .collect(Collectors.toSet());
    }

    @PostMapping("/{gameId}/move/{from}/to/{to}")
    @CrossOrigin(origins = "*")
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