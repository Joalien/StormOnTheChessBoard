package fr.kubys.controller;

import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Position;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.dto.PlayerDto;
import fr.kubys.mapper.ModelMapper;
import fr.kubys.repository.ChessBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static fr.kubys.core.Position.*;
import static fr.kubys.mapper.ModelMapper.mapToDto;

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
        try {
            return switch (playerId) {
                case "white" -> ModelMapper.map(chessBoardRepository.getChessBoardService(gameId).getWhite());
                case "black" -> ModelMapper.map(chessBoardRepository.getChessBoardService(gameId).getBlack());
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            };
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}