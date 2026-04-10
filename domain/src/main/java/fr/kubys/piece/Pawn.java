package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class Pawn extends Piece implements Promotable {

    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean isOnPromotionRow() {
        return switch (color) {
            case WHITE -> getRow() == Row.Eight;
            case BLACK -> getRow() == Row.One;
            case NONE -> getRow() == Row.One || getRow() == Row.Eight;
        };
    }

    public Optional<Position> twoSquaresForward() {
        if (color.isNeutral()) return Optional.empty();
        return forward().flatMap(this::nextRow)
                .map(row -> Position.posToSquare(getFile(), row));
    }

    public Optional<Position> oneSquareForward() {
        return forward().map(row -> Position.posToSquare(getFile(), row));
    }

    private Row startingRow() {
        return switch (color) {
            case WHITE -> Row.Two;
            case BLACK -> Row.Seven;
            case NONE -> null;
        };
    }

    private Optional<Row> forward() {
        return (color == Color.BLACK) ? getRow().previous() : getRow().next();
    }

    private Optional<Row> nextRow(Row row) {
        return (color == Color.BLACK) ? row.previous() : row.next();
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color targetPieceColor) {
        if (color.isNeutral()) {
            return checkDirection(file, row, targetPieceColor, getRow().next())
                    || checkDirection(file, row, targetPieceColor, getRow().previous());
        }
        return checkDirection(file, row, targetPieceColor, forward());
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color targetPieceColor, Color effectiveColor) {
        if (color.isNeutral()) {
            if (effectiveColor == Color.WHITE) return checkDirection(file, row, targetPieceColor, getRow().next());
            if (effectiveColor == Color.BLACK) return checkDirection(file, row, targetPieceColor, getRow().previous());
            return isPositionTheoreticallyReachable(file, row, targetPieceColor);
        }
        return isPositionTheoreticallyReachable(file, row, targetPieceColor);
    }

    private boolean checkDirection(File file, Row row, Color targetPieceColor, Optional<Row> forwardRow) {
        boolean moveTwoSquaresFromStart = getRow() == startingRow()
                && twoSquaresForward().map(pos -> pos.getRow() == row).orElse(false);
        boolean moveOneSquare = forwardRow.map(r -> r == row).orElse(false);
        boolean moveForward = targetPieceColor == null && file == getFile() && (moveTwoSquaresFromStart || moveOneSquare);

        boolean takePiece = moveOneSquare && getFile().distanceTo(file) == 1;
        boolean takeEnemyPiece = targetPieceColor != null && targetPieceColor != getColor() && takePiece;

        return moveForward || takeEnemyPiece;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (color.isNeutral()) return Collections.emptySet();
        boolean moveForwardTwoSquaresFromStart = isPositionTheoreticallyReachable(squareToMoveOn)
                && getRow() == startingRow()
                && twoSquaresForward().map(squareToMoveOn::equals).orElse(false);
        return moveForwardTwoSquaresFromStart
                ? oneSquareForward().map(Set::of).orElse(Collections.emptySet())
                : Collections.emptySet();
    }

    @Override
    public String toString() {
        return this.getColor().toString().toLowerCase() + " Pawn";
    }
}
