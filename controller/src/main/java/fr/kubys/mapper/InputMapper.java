package fr.kubys.mapper;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.QuadrilleCard;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class InputMapper {

    public static <T extends CardParam> T mapParamToCardParam(Map<String, Object> param, Class<T> clazz, ChessBoardReadService chessBoardService) {
        Set<String> inputParameterKeys = param.keySet();
        Set<String> modelKeys = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
        if (!inputParameterKeys.equals(modelKeys)) {
            throw new MappingException("Input dto %s does not match card parameter %s".formatted(inputParameterKeys, modelKeys));
        }

        List<Object> list = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> mapFieldToModel(param, chessBoardService, field))
                .toList();

        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(list.stream()
                    .map(Object::getClass)
                    .map(aClass -> aClass.equals(HashSet.class) ? Set.class : aClass) // FIXME
                    .toArray(Class[]::new));
            return declaredConstructor.newInstance(list.toArray());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object mapFieldToModel(Map<String, Object> param, ChessBoardReadService chessBoardService, Field field) {
        if (Position.class.equals(field.getType())) return Position.valueOf((String) param.get(field.getName()));
        if (Piece.class.isAssignableFrom(field.getType())) return getPieceFromChessboard(chessBoardService, (String) param.get(field.getName()));
        if (Collection.class.isAssignableFrom(field.getType())) return ((Collection<String>) param.get(field.getName())).stream()
                                                                            .map(pos -> getPieceFromChessboard(chessBoardService, pos))
                                                                            .collect(Collectors.toSet());
        if (QuadrilleCard.Direction.class.equals(field.getType())) return QuadrilleCard.Direction.valueOf((String) param.get(field.getName()));
        throw new MappingException("No mapping found for class %s".formatted(field.getType()));
    }

    private static Piece getPieceFromChessboard(ChessBoardReadService chessboard, String position) {
        return chessboard.getPieces().stream()
                .filter(piece -> piece.getPosition() == Position.valueOf(position))
                .findFirst()
                .orElseThrow();
    }
}
