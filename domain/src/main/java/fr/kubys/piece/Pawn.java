package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public abstract class Pawn extends Piece {

    protected Pawn(Color color) {
        super(color);
    }

    public abstract Optional<Position> twoSquaresForward();

    public abstract Optional<Position> oneSquareForward();

    protected abstract Row startingRow();

    public abstract boolean isOnPromotionRow();

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        boolean moveTwoSquaresFromStart = getRow() == startingRow()
                && twoSquaresForward().map(pos -> pos.getRow() == row).orElse(false);
        boolean moveOneSquare = oneSquareForward().map(pos -> pos.getRow() == row).orElse(false);
        boolean moveForward = color == null && file == getFile() && (moveTwoSquaresFromStart || moveOneSquare);

        boolean takePiece = moveOneSquare && getFile().distanceTo(file) == 1;
        boolean takeEnemyPiece = color != null && color != getColor() && takePiece;

        return moveForward || takeEnemyPiece;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
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
