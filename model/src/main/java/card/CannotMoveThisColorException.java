package card;

import core.Color;

public class CannotMoveThisColorException extends IllegalStateException {
    public CannotMoveThisColorException(Color color) {
        super("You cannot move %s piece".formatted(color));
    }
}
