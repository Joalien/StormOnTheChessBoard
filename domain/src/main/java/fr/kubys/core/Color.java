package fr.kubys.core;

public enum Color {
    BLACK,
    WHITE,
    NONE;

    public Color opposite() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
            case NONE -> throw new RuntimeException("Color NONE has no opposite");
        };
    }
}
