package piece;

import position.PositionUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class Piece {

    protected Color color;
    protected char type;
    private Square square;

    public Piece(Color color, char typePiece) {
        this.color = color;
        this.type = typePiece;
    }

    public int getY() {
        return PositionUtil.getY(square.getPosition());
    }

    public int getX() {
        return PositionUtil.getX(square.getPosition());
    }

    public char getType() {
        return this.type;
    }

    public boolean isPositionTheoricallyReachable(String s) {
        return isPositionTheoricallyReachable(PositionUtil.getX(s), PositionUtil.getY(s), Optional.empty());
    }

    public abstract boolean isPositionTheoricallyReachable(int x, int y, Optional<Color> color);

    public boolean isPositionTheoricallyReachable(String s, Optional<Color> color) {
        return isPositionTheoricallyReachable(PositionUtil.getX(s), PositionUtil.getY(s), color);
    }

    public abstract Set<String> squaresOnThePath(String squareToMoveOn);

    public String getPosition() {
        return this.getSquare().map(Square::getPosition).orElse(null);
    }

    public Optional<Square> getSquare() {
        return Optional.ofNullable(square);
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    @Override
    public String toString() {
        String color = Optional.ofNullable(this.getColor())
                .map(Objects::toString)
                .map(String::toLowerCase)
                .orElse("");
        String pieceName = this.getClass().getSimpleName();
        return String.format("%s %s",
                color, pieceName);
    }

    public Color getColor() {
        return color;
    }
}