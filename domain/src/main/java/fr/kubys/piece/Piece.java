package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Piece {

    protected Color color;
    private Position position;

    public Predicate<Stream<Optional<Piece>>> hasEmptyPath() {
        return path -> path.allMatch(Optional::isEmpty);
    }

    public Piece(Color color) {
        this.color = color;
    }

    public File getFile() {
        return position.getFile();
    }

    public Row getRow() {
        return position.getRow();
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
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
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
            p.setPosition(position);
            return p;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        String color = Optional.ofNullable(this.getColor())
                .map(Enum::toString)
                .map(String::toLowerCase)
                .orElse("");
        String pieceName = this.getClass().getSimpleName();
        return "%s %s".formatted(color, pieceName);
    }

    public Color getColor() {
        return color;
    }

    public abstract boolean isKing();
}