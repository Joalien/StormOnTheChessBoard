package card;

import piece.Color;

public class IllegalColorException extends IllegalStateException {
    public IllegalColorException(Color color) {
        super("You cannot move %s piece".formatted(color));
    }
}
