package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Rock extends Piece implements Castlable {

    private boolean canCastle = true;

    public Rock(Color color) {
        super(color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        if (getFile() == file && getRow() == row) return false; // fail
        return (getRow() == row) || (getFile() == file);
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (!super.isPositionTheoreticallyReachable(squareToMoveOn)) return Collections.emptySet();

        if (getFile() == squareToMoveOn.getFile()) {
            return squaresOnTheFile(getPosition(), squareToMoveOn);
        } else if (getRow() == squareToMoveOn.getRow()) {
            return squaresOnTheRow(getPosition(), squareToMoveOn);
        } else throw new IllegalStateException();
    }

    static Set<Position> squaresOnTheFile(Position currentPosition, Position squareToMoveOn) {
        Set<Position> squaresOnThePath = new HashSet<>();
        int firstRow = Math.min(currentPosition.getRow().getRowNumber(), squareToMoveOn.getRow().getRowNumber());
        int lastRow = Math.max(currentPosition.getRow().getRowNumber(), squareToMoveOn.getRow().getRowNumber());
        for (int y = firstRow + 1; y < lastRow; y++) {
            squaresOnThePath.add(Position.posToSquare(currentPosition.getFile(), Row.fromNumber(y)));
        }
        return squaresOnThePath;
    }

    static Set<Position> squaresOnTheRow(Position currentPosition, Position squareToMoveOn) {
        Set<Position> squaresOnThePath = new HashSet<>();
        int firstFile = Math.min(currentPosition.getFile().getFileNumber(), squareToMoveOn.getFile().getFileNumber());
        int lastFile = Math.max(currentPosition.getFile().getFileNumber(), squareToMoveOn.getFile().getFileNumber());
        for (int x = firstFile + 1; x < lastFile; x++) {
            squaresOnThePath.add(Position.posToSquare(File.fromNumber(x), currentPosition.getRow()));
        }
        return squaresOnThePath;
    }

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public boolean canCastle() {
        return canCastle;
    }

    @Override
    public void cannotCastleAnymore() {
        canCastle = false;
    }
}