package fr.kubys.mapper;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.card.params.CardParamException;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class CardParametersMapper {

    public static <T extends CardParam> Card<T> checkThatCardParametersMatch(Card<? extends CardParam> cardMatchingName) {
        try {
            return (Card<T>) cardMatchingName;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Paramètres incompatibles avec cette carte");
        }
    }

    public static <T extends CardParam> T mapParamToCardParam(Map<String, Object> param, Class<T> clazz, ChessBoardReadService chessBoardService) {
        Set<String> inputParameterKeys = param.keySet();
        Set<String> modelKeys = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
        if (!inputParameterKeys.equals(modelKeys)) {
            throw new CardParamException("Le paramètre %s ne correspond pas au paramètre de carte %s".formatted(inputParameterKeys, modelKeys));
        }

        List<Object> list = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> mapFieldToModel(param, chessBoardService, field))
                .toList();

        try {
            return (T) clazz.getDeclaredConstructors()[0].newInstance(list.toArray()); // pray it works
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object mapFieldToModel(Map<String, Object> param, ChessBoardReadService chessBoardService, Field field) {
        if (Position.class.equals(field.getType())) return Position.valueOf((String) param.get(field.getName()));
        if (Piece.class.isAssignableFrom(field.getType()))
            return getPieceFromChessboard(chessBoardService, (String) param.get(field.getName()));
        if (Collection.class.isAssignableFrom(field.getType())) {
            Class<?> elementType = getCollectionElementType(field);
            Collection<String> rawValues = (Collection<String>) param.get(field.getName());
            if (Position.class.isAssignableFrom(elementType)) {
                var positions = rawValues.stream().map(Position::valueOf);
                if (List.class.isAssignableFrom(field.getType()))
                    return positions.collect(Collectors.toList());
                return positions.collect(Collectors.toSet());
            }
            var pieces = rawValues.stream()
                    .map(pos -> getPieceFromChessboard(chessBoardService, pos));
            if (List.class.isAssignableFrom(field.getType()))
                return pieces.collect(Collectors.toList());
            return pieces.collect(Collectors.toSet());
        }
        if (Effect.class.isAssignableFrom(field.getType()))
            return getEffectFromChessboard(chessBoardService, (String) param.get(field.getName()));
        if (field.getType().isEnum()) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Enum<?> enumValue = Enum.valueOf((Class<? extends Enum>) field.getType(), (String) param.get(field.getName()));
            return enumValue;
        }
        throw new CardParamException("Aucun mapping trouvé pour la classe %s".formatted(field.getType()));
    }

    private static Effect getEffectFromChessboard(ChessBoardReadService chessboard, String value) {
        Position position = Position.valueOf(value);
        return chessboard.getEffects().stream()
                .filter(e -> e.getPositions().contains(position))
                .findFirst()
                .orElseThrow(() -> new CardParamException("Aucun effet trouvé sur la case %s".formatted(value)));
    }

    private static Class<?> getCollectionElementType(Field field) {
        if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
            var typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length == 1 && typeArgs[0] instanceof Class<?> clazz) {
                return clazz;
            }
        }
        return Object.class;
    }

    private static Piece getPieceFromChessboard(ChessBoardReadService chessboard, String value) {
        if (value.startsWith("captured:")) {
            int index = Integer.parseInt(value.substring("captured:".length()));
            List<Piece> captured = chessboard.getCapturedPieces();
            if (index < 0 || index >= captured.size())
                throw new CardParamException("Index de pièce capturée invalide %d".formatted(index));
            return captured.get(index);
        }
        return chessboard.getPieces().stream()
                .filter(piece -> piece.getPosition() == Position.valueOf(value))
                .findFirst()
                .orElseThrow(() -> new CardParamException("No piece found on square %s".formatted(value)));
    }
}
