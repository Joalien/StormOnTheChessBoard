package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.card.params.CardParam;
import fr.kubys.command.*;
import fr.kubys.core.Position;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.mapper.MappingException;
import fr.kubys.repository.ChessBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static fr.kubys.core.Position.*;
import static fr.kubys.mapper.OutputMapper.mapToDto;

@RestController
@RequestMapping("/chessboard")
public class GameController {

    ChessBoardRepository chessBoardRepository;

    @Autowired
    public GameController(ChessBoardRepository chessBoardRepository) {
        this.chessBoardRepository = chessBoardRepository;
        createInitialState(); // FIXME remove me later on
    }

    private void createInitialState() {
        Integer gameId = chessBoardRepository.createNewGame();
        List.of(List.of(e2, e4),
                List.of(c7, c5),
                List.of(g1, f3),
                List.of(d7, d6),
                List.of(d2, d4),
                List.of(c5, d4),
                List.of(f3, d4),
                List.of(g8, f6),
                List.of(c1, e3),
                List.of(g7, g6),
                List.of(b1, c3),
                List.of(f8, g7),
                List.of(d1, d2),
                List.of(e8, g8),
                List.of(e1, c1),
                List.of(a7, a6)
        ).forEach(m -> saveCommand(m, gameId));
    }

    private void saveCommand(List<Position> m, Integer gameId) {
        chessBoardRepository.saveCommand(PlayMoveCommand.builder()
                .gameId(gameId)
                .from(m.get(0))
                .to(m.get(1)).build());
        chessBoardRepository.saveCommand(EndTurnCommand.builder().gameId(gameId).build());
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
        return ResponseEntity.ok().build();
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
        return ResponseEntity.ok().build();
    }
}