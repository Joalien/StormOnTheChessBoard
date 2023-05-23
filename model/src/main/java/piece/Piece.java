package piece;

import position.File;
import position.Position;
import position.Row;

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

    public File getFile() {
        return square.getPosition().getFile();
    }

    public Row getRow() {
        return square.getPosition().getRow();
    }

    public char getType() {
        return this.type;
    }

    public boolean isPositionTheoreticallyReachable(Position position) {
        return isPositionTheoreticallyReachable(position.getFile(), position.getRow(), null);
    }

    public abstract boolean isPositionTheoreticallyReachable(File file, Row row, Color color);

    public boolean isPositionTheoreticallyReachable(Position position, Color color) {
        return isPositionTheoreticallyReachable(position.getFile(), position.getRow(), color);
    }

    public abstract Set<Position> squaresOnThePath(Position squareToMoveOn);

    public Position getPosition() {
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
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
        return "%s %s".formatted(color, pieceName);
    }

    public Color getColor() {
        return color;
    }
}