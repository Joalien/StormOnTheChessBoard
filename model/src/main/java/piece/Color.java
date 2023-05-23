package piece;

public enum Color {
    BLACK,
    WHITE;

    public Color opposite() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
        };
    }
}
