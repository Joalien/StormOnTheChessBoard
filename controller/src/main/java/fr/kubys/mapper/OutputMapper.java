package fr.kubys.mapper;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.effect.*;
import fr.kubys.card.Card;
import fr.kubys.card.CardRegistry;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.dto.CardOutputDto;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.dto.EffectDto;
import fr.kubys.dto.PlayerDto;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.Crab;
import fr.kubys.piece.extra.Kangaroo;
import fr.kubys.player.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OutputMapper {
    private static final Map<String, Card<?>> EFFECT_TO_CARD = CardRegistry.createAllCards().stream()
            .collect(Collectors.toMap(
                    c -> c.getClass().getSimpleName().replace("Card", "Effect"),
                    c -> c,
                    (a, b) -> a
            ));
    public static ChessBoardDto mapToDto(Integer gameId, ChessBoardReadService chessBoard) {
        return ChessBoardDto.builder()
                .id(gameId)
                .effects(chessBoard.getEffects().stream().map(OutputMapper::map).collect(Collectors.toSet()))
                .deck(chessBoard.getStack().stream().map(OutputMapper::map).collect(Collectors.toSet()))
                .discard(chessBoard.getDiscard().stream().map(OutputMapper::map).collect(Collectors.toSet()))
                .whitePlayer(map(chessBoard.getWhite()))
                .blackPlayer(map(chessBoard.getBlack()))
                .currentTurn(chessBoard.getCurrentPlayer().getColor().name().toLowerCase())
                .currentState(chessBoard.getCurrentStateName())
                .pieces(chessBoard.getPieces().stream().collect(Collectors.toMap(piece -> piece.getPosition().name(), OutputMapper::map)))
                .pendingPromotions(chessBoard.getPendingPromotions().stream().map(p -> p.name()).collect(Collectors.toSet()))
                .capturedPieces(chessBoard.getCapturedPieces().stream().map(OutputMapper::map).toList())
                .checkMateTargets(chessBoard.getPieces().stream()
                        .filter(p -> p.isKing() && !(p instanceof King))
                        .map(p -> p.getPosition().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    public static EffectDto map(Effect e) {
        EffectDto.EffectDtoBuilder builder = EffectDto.builder()
                .name(e.getClass().getSimpleName())
                .positions(e.getPositions());
        if (e instanceof BarricadeEffect barricade) {
            builder.edges(barricade.getEdges().stream()
                    .map(edge -> edge.stream().map(Position::name).toList())
                    .toList());
        }
        if (e instanceof NeutralityEffect ne && ne.getPiece().getPosition() != null) {
            builder.positions(java.util.Set.of(ne.getPiece().getPosition()));
        } else if (e instanceof KangarooEffect ke && ke.getPiece().getPosition() != null) {
            builder.positions(java.util.Set.of(ke.getPiece().getPosition()));
        } else if (e instanceof CrabEffect ce && ce.getPiece().getPosition() != null) {
            builder.positions(java.util.Set.of(ce.getPiece().getPosition()));
        }
        Card<?> card = EFFECT_TO_CARD.get(e.getClass().getSimpleName());
        if (card != null) {
            builder.cardName(card.getName())
                    .cardEnglishName(card.getClass().getSimpleName())
                    .cardDescription(card.getDescription());
        }
        return builder.build();
    }

    public static <T extends CardParam> CardOutputDto map(Card<T> c) {
        Map<String, Object> cardParamOutputDto = Arrays.stream(c.getClazz().getDeclaredFields())
                .map(Field::getName)
                .collect(HashMap::new, (hashMap, name) -> hashMap.put(name, null), HashMap::putAll);
        return CardOutputDto.builder()
                .name(c.getName())
                .englishName(c.getClass().getSimpleName())
                .description(c.getDescription())
                .type(c.getType())
                .param(cardParamOutputDto)
                .build();
    }

    public static PlayerDto map(Player p) {
        return PlayerDto.builder()
                .name(p.getName())
                .color(p.getColor())
                .cards(p.getCards().stream().map(OutputMapper::map).toList())
                .build();
    }

    public static String map(Piece piece) {
        String pieceType = Map.<Predicate<Piece>, String>of(
                        p -> p instanceof Pawn, "P",
                        p -> p instanceof King, "K",
                        p -> p instanceof Queen, "Q",
                        p -> p instanceof Knight, "N",
                        p -> p instanceof Bishop, "B",
                        p -> p instanceof Rock, "R",
                        p -> p instanceof Kangaroo, "Kangaroo",
                        p -> p instanceof Crab, "Crab"
                ).entrySet().stream()
                .filter(objectStringEntry -> objectStringEntry.getKey().test(piece))
                .findAny()
                .map(Map.Entry::getValue)
                .orElseThrow();
        Map<Color, String> colorPrefix = Map.of(
                Color.WHITE, "w",
                Color.BLACK, "b",
                Color.NONE, ""
        );
        return colorPrefix.get(piece.getColor()) + pieceType;
    }
}
