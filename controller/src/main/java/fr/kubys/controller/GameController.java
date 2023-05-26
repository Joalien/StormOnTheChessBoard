package fr.kubys.controller;

import fr.kubys.dto.ChessBoardDto;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.command.StartGameCommand;
import fr.kubys.core.Position;
import fr.kubys.repository.ChessBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static fr.kubys.mapper.ModelMapper.mapToDto;
import static fr.kubys.core.Position.*;

@RestController
public class GameController {

    ChessBoardRepository chessBoardRepository;

    @Autowired
    public GameController(ChessBoardRepository chessBoardRepository) {
        this.chessBoardRepository = chessBoardRepository;
        createInitialState(); // FIXME remove me later on
    }

    private void createInitialState() {
        Integer gameId = 1;
        chessBoardRepository.saveCommand(StartGameCommand.builder().gameId(gameId).build());
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
                List.of(e1, c1)
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
    @GetMapping("/chessboard/{id}")
    @CrossOrigin(origins = "*")
    public ChessBoardDto getGameById(@PathVariable Integer id) {
        try {
            return mapToDto(id, chessBoardRepository.getChessBoardService(id));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
        }
    }
}