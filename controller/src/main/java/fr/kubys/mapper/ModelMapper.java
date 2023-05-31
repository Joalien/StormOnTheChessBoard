package fr.kubys.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import fr.kubys.card.params.CardParam;
import fr.kubys.card.params.CourtlyLoveCardParam;
import fr.kubys.core.Position;
import fr.kubys.dto.CardOutputDto;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.dto.EffectDto;
import fr.kubys.dto.PlayerDto;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.Card;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import fr.kubys.player.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModelMapper {
    public static ChessBoardDto mapToDto(Integer gameId, ChessBoardReadService chessBoard) {
        return ChessBoardDto.builder()
                .id(gameId)
                .effects(chessBoard.getEffects().stream().map(ModelMapper::map).collect(Collectors.toSet()))
                .deck(chessBoard.getCards().stream().map(ModelMapper::map).collect(Collectors.toSet()))
                .whitePlayer(map(chessBoard.getWhite()))
                .blackPlayer(map(chessBoard.getBlack()))
                .pieces(chessBoard.getPieces().stream().collect(Collectors.toMap(piece -> piece.getPosition().name(), ModelMapper::map)))
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
                .cards(p.getCards().stream().map(ModelMapper::map).collect(Collectors.toList()))
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

    public static <T extends CardParam> T mapCardDtoToCardParam(Map<String, String> param, Class<T> clazz, ChessBoardReadService chessBoardService) {
        List<Object> list = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    if (field.getType().equals(String.class)) return param.get(field.getName());
                    if (field.getType().equals(Position.class)) return Position.valueOf(param.get(field.getName()));
                    if (field.getType().equals(Piece.class)) return getPieceFromChessboard(chessBoardService, param.get(field.getName()));
                    if (field.getType().equals(Knight.class)) return (Knight) getPieceFromChessboard(chessBoardService, param.get(field.getName()));
                    throw new RuntimeException();
                }).toList();

        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(list.stream().map(Object::getClass).toArray(Class[]::new));
            return declaredConstructor.newInstance(list.toArray());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Piece getPieceFromChessboard(ChessBoardReadService chessboard, String position) {
        return chessboard.getPieces().stream()
                .filter(piece -> piece.getPosition() == Position.valueOf(position))
                .findFirst()
                .orElseThrow();
    }
}
