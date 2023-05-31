package fr.kubys.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.Card;
import fr.kubys.card.CardNotFoundException;
import fr.kubys.card.params.CardParam;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayCardCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Position;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.mapper.MappingException;
import fr.kubys.mapper.ModelMapper;
import fr.kubys.repository.ChessBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.kubys.core.Position.*;
import static fr.kubys.mapper.ModelMapper.mapToDto;

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
        try {
            chessBoardRepository.saveCommand(endTurnCommand);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{gameId}")
    @CrossOrigin(origins = "*")
    public ChessBoardDto getGameById(@PathVariable Integer gameId) {
        return mapToDto(gameId, chessBoardRepository.getChessBoardService(gameId));
    }

    @PostMapping("/{gameId}/card/{cardName}")
    @CrossOrigin(origins = "*")
    public <T extends CardParam> ResponseEntity<Void> updateGame(@PathVariable Integer gameId, @PathVariable String cardName, @RequestBody Map<String, String> param) {
        ChessBoardReadService chessBoardService = chessBoardRepository.getChessBoardService(gameId);
        Card<T> card = chessBoardService.getCurrentPlayer().getCards().stream()
                .filter(c -> Objects.equals(c.getName(), cardName))// FIXME migrate to id
                .findFirst()
                .map(GameController::<T>checkThatCardParametersMatch)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "%s not in user hand!".formatted(cardName)));
        try {
            T parameters = ModelMapper.mapCardDtoToCardParam(param, card.getClazz(), chessBoardService);
            PlayCardCommand<T> command = PlayCardCommand.<T>builder()
                    .gameId(gameId)
                    .parameters(parameters)
                    .card(card)
                    .build();
            chessBoardRepository.saveCommand(command);
            return ResponseEntity.ok().build();
        } catch (MappingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private static <T extends CardParam> Card<T> checkThatCardParametersMatch(Card<? extends CardParam> cardMatchingName) {
        try {
            return (Card<T>) cardMatchingName;
        } catch (ClassCastException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameters do not match");
        }
    }

    @PostMapping("/{gameId}/move/{from}/to/{to}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> move(@PathVariable Integer gameId, @PathVariable String from, @PathVariable String to) {
        Command command = PlayMoveCommand.builder()
                .gameId(gameId)
                .from(Position.valueOf(from))
                .to(Position.valueOf(to))
                .build();
        try {
            chessBoardRepository.saveCommand(command);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}