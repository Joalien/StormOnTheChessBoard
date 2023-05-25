package fr.kubys.StormOnTheChessBoard.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.Card;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.command.StartGameCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.StormOnTheChessBoard.dto.CardOutputDto;
import fr.kubys.StormOnTheChessBoard.dto.ChessBoardDto;
import fr.kubys.StormOnTheChessBoard.dto.EffectDto;
import fr.kubys.StormOnTheChessBoard.dto.PlayerDto;
import fr.kubys.piece.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.ChessBoardRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

import static fr.kubys.core.Position.*;

@RestController
public class GameController {

    public static final Integer GAME_ID = 1;
    ChessBoardRepository chessBoardRepository;

    @GetMapping("/chessboard/{id}")
    @CrossOrigin(origins = "*")
    public ChessBoardDto getGameById(@PathVariable Integer id) {
        return mapToDto(id, chessBoardRepository.getChessBoardService(id));
    }

    public GameController() {
        this.chessBoardRepository = new ChessBoardRepositoryImpl();

        chessBoardRepository.saveCommand(StartGameCommand.builder().gameId(GAME_ID).build());

        List.of(List.of(e2, e4),
            List.of(c7, c5),
            List.of(g1, f3),
            List.of(d7, d6),
            List.of(d2, d4),
            List.of(c5, d4),
            List.of(f3, d4),
            List.of(g8, f6),
            List.of(c1, e3),
            List.of(g7, g6)
        ).forEach(this::saveCommand);
    }

    private void saveCommand(List<Position> m) {
        chessBoardRepository.saveCommand(PlayMoveCommand.builder()
                .gameId(GAME_ID)
                .from(m.get(0))
                .to(m.get(1)).build());
        chessBoardRepository.saveCommand(EndTurnCommand.builder().gameId(GAME_ID).build());
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