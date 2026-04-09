package fr.kubys.core;

public enum Color {
    BLACK,
    WHITE,
    NONE;

    public boolean cannotBeMovedBy(Color playerColor) {
        return this != playerColor && this != NONE;
    }

    public boolean isNeutral() {
        return this == NONE;
    }

    public Color resolveFor(Color currentTurn) {
        return this == NONE ? currentTurn : this;
    }

    public Color opposite() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
            case NONE -> throw new RuntimeException("Color NONE has no opposite");
        };
    }
}
