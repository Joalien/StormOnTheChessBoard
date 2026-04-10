package fr.kubys.core;

public enum Color {
    BLACK(Row.Eight),
    WHITE(Row.One),
    NONE(null);

    private final Row homeRow;

    Color(Row homeRow) {
        this.homeRow = homeRow;
    }

    public Row homeRow() {
        if (this == NONE) throw new RuntimeException("Color NONE has no home row");
        return homeRow;
    }

    public boolean cannotBeMovedBy(Color playerColor) {
        return this != playerColor && this != NONE;
    }

    public boolean isNeutral() {
        return this == NONE;
    }

    public Color opposite() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
            case NONE -> throw new RuntimeException("Color NONE has no opposite");
        };
    }
}
