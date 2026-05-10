package fr.kubys.ai.card;

import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Inverse of {@code CardParametersMapper.mapParamToCardParam}: converts a typed CardParam
 * into the {@code Map<String, Object>} representation expected by
 * {@code PlayCardWithImmutableParamCommand}. This is what lets the AI emit a card-play
 * command that survives replay — the live mapper re-resolves piece references against
 * each fresh ChessBoard, so the command stays valid even though we've reconstructed the
 * board state from scratch.
 */
public final class CardParamSerializer {

    private CardParamSerializer() {}

    public static Map<String, Object> toMap(CardParam param) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                result.put(field.getName(), encode(field.get(param)));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot read field " + field.getName(), e);
            }
        }
        return result;
    }

    private static Object encode(Object value) {
        if (value == null) return null;
        if (value instanceof Position position) return position.name();
        if (value instanceof Piece piece) {
            if (piece.getPosition() == null) {
                throw new IllegalStateException("Cannot serialize off-board piece without position: " + piece);
            }
            return piece.getPosition().name();
        }
        if (value instanceof Enum<?> enumValue) return enumValue.name();
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(CardParamSerializer::encode).collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Unsupported CardParam field type: " + value.getClass());
    }
}
