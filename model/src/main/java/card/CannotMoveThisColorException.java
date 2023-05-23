package card;

import piece.Color;

public class CannotMoveThisColorException extends IllegalStateException {
    public CannotMoveThisColorException(Color color) {
        super("You cannot move %s piece".formatted(color));
    }
}
