package fr.kubys.StormOnTheChessBoard.controller;

import fr.kubys.StormOnTheChessBoard.dto.CardOutputDto;
import fr.kubys.StormOnTheChessBoard.dto.ChessBoardDto;
import fr.kubys.StormOnTheChessBoard.dto.EffectDto;
import fr.kubys.StormOnTheChessBoard.dto.PlayerDto;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.Card;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.command.StartGameCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.*;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.kubys.core.Position.*;

@RestController
public class GameController {

    public static final Integer GAME_ID = 1;

    ChessBoardRepository chessBoardRepository;

    @Autowired
    public GameController(ChessBoardRepository chessBoardRepository) {
        chessBoardRepository.saveCommand(StartGameCommand.builder().gameId(GAME_ID).build());

        List.of(List.of(e2, e4), // FIXME remove on
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
        ).forEach(this::saveCommand);
    }

    private void saveCommand(List<Position> m) {
        chessBoardRepository.saveCommand(PlayMoveCommand.builder()
                .gameId(GAME_ID)
                .from(m.get(0))
                .to(m.get(1)).build());
        chessBoardRepository.saveCommand(EndTurnCommand.builder().gameId(GAME_ID).build());
    }

    @GetMapping("/chessboard/{id}")
    @CrossOrigin(origins = "*")
    public ChessBoardDto getGameById(@PathVariable Integer id) {
        return mapToDto(id, chessBoardRepository.getChessBoardService(id));
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
                .description(c.getDescription())
                .type(c.getType())
                .build();
    }

    static PlayerDto map(Player p) {
        return PlayerDto.builder()
                .name(p.getName())
                .color(p.getColor())
                .build();
    }

    static String map(Piece piece) {
        String pieceType = Map.<Predicate<Piece>, String>of(
                        p -> p instanceof Pawn, "P",
                        p -> p instanceof King, "K",
                        p -> p instanceof Queen, "Q",
                        p -> p instanceof Knight, "N",
                        p -> p instanceof Bishop, "B",
                        p -> p instanceof Rock, "R"
                ).entrySet().stream()
                .filter(objectStringEntry -> objectStringEntry.getKey().test(piece))
                .findAny()
                .map(Map.Entry::getValue)
                .orElseThrow();
        return (piece.getColor() == Color.WHITE ? "w" : "b") + pieceType;
    }
}