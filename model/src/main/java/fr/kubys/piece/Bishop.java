package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        if (getFile() == file && getRow() == row) return false;
        return getRow().distanceTo(row) == getFile().distanceTo(file);
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (!super.isPositionTheoreticallyReachable(squareToMoveOn)) return Collections.emptySet();

        return squaresOnTheDiagonal(getPosition(), squareToMoveOn);
    }

    static Set<Position> squaresOnTheDiagonal(Position currentPosition, Position squareToMoveOn) {
        Set<Position> squaresOnThePath = new HashSet<>();
        boolean fileSign = currentPosition.getFile().isBefore(squareToMoveOn.getFile());
        boolean rowFile = currentPosition.getRow().isBefore(squareToMoveOn.getRow());
        for (int i = 1; i < currentPosition.getFile().distanceTo(squareToMoveOn.getFile()); i++) {
            squaresOnThePath.add(Position.posToSquare(currentPosition.getFile().getFileNumber() + i * (fileSign ? 1 : -1), currentPosition.getRow().getRowNumber() + i * (rowFile ? 1 : -1)));
        }
        return squaresOnThePath;
    }

    @Override
    public boolean isKing() {
        return false;
    }
}