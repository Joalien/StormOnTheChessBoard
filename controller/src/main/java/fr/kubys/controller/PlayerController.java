package fr.kubys.controller;

import fr.kubys.dto.PlayerDto;
import fr.kubys.mapper.OutputMapper;
import fr.kubys.repository.ChessBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/chessboard/{gameId}/players")
public class PlayerController {

    ChessBoardRepository chessBoardRepository;

    @Autowired
    public PlayerController(ChessBoardRepository chessBoardRepository) {
        this.chessBoardRepository = chessBoardRepository;
    }

    @GetMapping("/{playerId}")
    @CrossOrigin(origins = "*")
    public PlayerDto getPlayerById(@PathVariable Integer gameId, @PathVariable String playerId) {
        return switch (playerId) {
            case "white" -> OutputMapper.map(chessBoardRepository.getChessBoardService(gameId).getWhite());
            case "black" -> OutputMapper.map(chessBoardRepository.getChessBoardService(gameId).getBlack());
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        };
    }
}