package fr.kubys.StormOnTheChessBoard.controller;

import api.ChessBoardReadService;
import board.effect.Effect;
import card.Card;
import command.PlayMoveCommand;
import command.StartGameCommand;
import core.Color;
import fr.kubys.StormOnTheChessBoard.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import piece.*;
import player.Player;
import repository.ChessBoardRepository;
import repository.ChessBoardRepositoryImpl;

import java.util.stream.Collectors;

import static core.Position.e2;
import static core.Position.e4;

@RestController
public class GameController {

    ChessBoardRepository chessBoardRepository;

    @GetMapping("/chessboard/{id}")
    public ChessBoardDto getGameById(@PathVariable Integer id) {
        return mapToDto(id, chessBoardRepository.getChessBoardService(id));
    }

    public GameController() {
        this.chessBoardRepository = new ChessBoardRepositoryImpl();

        Integer gameId = 1;
        chessBoardRepository.saveCommand(gameId, StartGameCommand.builder().gameId(gameId).build());
        System.out.println(mapToDto(gameId, chessBoardRepository.getChessBoardService(gameId)).toString());

        chessBoardRepository.saveCommand(gameId, PlayMoveCommand.builder()
                .gameId(gameId)
                .from(e2)
                .to(e4).build());
        System.out.println(mapToDto(gameId, chessBoardRepository.getChessBoardService(gameId)).toString());
    }

    static ChessBoardDto mapToDto(Integer gameId, ChessBoardReadService chessBoard) {
        return ChessBoardDto.builder()
                .id(gameId)
                .effects(chessBoard.getEffects().stream().map(GameController::map).collect(Collectors.toSet()))
                .deck(chessBoard.getCards().stream().map(GameController::map).collect(Collectors.toSet()))
                .whitePlayer(map(chessBoard.getWhite()))
                .blackPlayer(map(chessBoard.getBlack()))
                .pieces(chessBoard.getPieces().stream().collect(Collectors.toMap(Piece::getPosition, GameController::map)))
                .build();
    }

    static EffectDto map(Effect c) {
        return EffectDto.builder()
                .name(c.getName())
                .build();
    }

    static CardOutputDto map(Card c) {
        return CardOutputDto.builder()
                .name(c.getName())
//                .description(c.getDescription())
                .type(c.getType())
                .build();
    }

    static PlayerDto map(Player p) {
        return PlayerDto.builder()
                .name(p.getName())
                .color(p.getColor())
                .build();
    }

    static String map(Piece p) {
        String pieceType = switch (p) {
            case Pawn pawn -> "P";
            case King k -> "K";
            case Queen q -> "Q";
            case Knight knight -> "N";
            case Bishop b -> "B";
            case Rock r -> "R";
            default -> "?";
        };
        return (p.getColor() == Color.WHITE? "w" : "b") + pieceType;
    }
}