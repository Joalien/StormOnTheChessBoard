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

    /**
     * Returns the effective color of this piece for the given turn.
     * For standard pieces, this is simply their color.
     * Neutral pieces (Color.NONE) are controlled by whoever's turn it is.
     */
    public Color getEffectiveColor(Color currentTurn) {
        return color.isNeutral() ? currentTurn : color;
    }

    /**
     * Same as {@link #isPositionTheoreticallyReachable(File, Row, Color)} but with the effective
     * color of the piece, allowing neutral pieces to adapt their behavior (e.g. pawn direction).
     * Most pieces ignore effectiveColor — only neutral pieces override this.
     */
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color targetPieceColor, Color effectiveColor) {
        return isPositionTheoreticallyReachable(file, row, targetPieceColor);
    }

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

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract boolean isKing();
}