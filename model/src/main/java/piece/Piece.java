package piece;

import position.PositionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    public boolean isPositionTheoreticallyReachable(String s) {
        return isPositionTheoreticallyReachable(PositionUtil.getX(s), PositionUtil.getY(s), Optional.empty());
    }

    public abstract boolean isPositionTheoreticallyReachable(int x, int y, Optional<Color> color);

    public boolean isPositionTheoreticallyReachable(String s, Optional<Color> color) {
        return isPositionTheoreticallyReachable(PositionUtil.getX(s), PositionUtil.getY(s), color);
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

    public Piece clone() {
        try {
            Piece p;
            Constructor<? extends Piece> firstConstructor = (Constructor<? extends Piece>) this.getClass().getConstructors()[0];
            if (firstConstructor.getParameterTypes().length == 0) {
                p = firstConstructor.newInstance();
            } else if (firstConstructor.getParameterTypes()[0] == Color.class) {
                p = this.getClass().getConstructor(Color.class).newInstance(color);
            } else {
                throw new InstantiationException("Constructor not found");
            }
            p.setSquare(square);
            return p;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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