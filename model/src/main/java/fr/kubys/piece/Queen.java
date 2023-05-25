package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Set;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        if (getFile() == file && getRow() == row) return false;
        return (getRow().distanceTo(row) == getFile().distanceTo(file)) || ((getRow() == row) || (getFile() == file));
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (!super.isPositionTheoreticallyReachable(squareToMoveOn)) return Collections.emptySet();

        if (getFile() == squareToMoveOn.getFile()) {
            return Rock.squaresOnTheFile(getPosition(), squareToMoveOn);
        } else if (getRow() == squareToMoveOn.getRow()) {
            return Rock.squaresOnTheRow(getPosition(), squareToMoveOn);
        } else {
            return Bishop.squaresOnTheDiagonal(getPosition(), squareToMoveOn);
        }
    }

    @Override
    public boolean isKing() {
        return false;
    }
}