package fr.kubys.mapper;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.Card;
import fr.kubys.core.Color;
import fr.kubys.dto.CardOutputDto;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.dto.EffectDto;
import fr.kubys.dto.PlayerDto;
import fr.kubys.piece.*;
import fr.kubys.player.Player;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OutputMapper {
    public static ChessBoardDto mapToDto(Integer gameId, ChessBoardReadService chessBoard) {
        return ChessBoardDto.builder()
                .id(gameId)
                .effects(chessBoard.getEffects().stream().map(OutputMapper::map).collect(Collectors.toSet()))
                .deck(chessBoard.getCards().stream().map(OutputMapper::map).collect(Collectors.toSet()))
                .whitePlayer(map(chessBoard.getWhite()))
                .blackPlayer(map(chessBoard.getBlack()))
                .pieces(chessBoard.getPieces().stream().collect(Collectors.toMap(piece -> piece.getPosition().name(), OutputMapper::map)))
                .build();
    }

    public static EffectDto map(Effect c) {
        return EffectDto.builder()
                .name(c.getName())
                .build();
    }

    public static CardOutputDto map(Card c) {
        return CardOutputDto.builder()
                .name(c.getName())
                .description(c.getDescription())
                .type(c.getType())
                .build();
    }

    public static PlayerDto map(Player p) {
        return PlayerDto.builder()
                .name(p.getName())
                .color(p.getColor())
                .cards(p.getCards().stream().map(OutputMapper::map).collect(Collectors.toList()))
                .build();
    }

    public static String map(Piece piece) {
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
