package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Set;

public class Knight extends Piece {

    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        boolean isNotOnTheSameLine = getFile() != file;
        boolean isNotOnTheSameFile = getRow() != row;
        boolean distanceOfThree = getFile().distanceTo(file) + getRow().distanceTo(row) == 3;
        return isNotOnTheSameLine && isNotOnTheSameFile && distanceOfThree;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        return Collections.emptySet(); // piece.Knight can jump over pieces
    }

    @Override
    public boolean isKing() {
        return false;
    }
}