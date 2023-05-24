package piece;

import position.File;
import position.Position;
import position.Row;

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

        Set<Position> squaresOnThePath = new HashSet<>();
        if (getFile() == squareToMoveOn.getFile()) {
            int firstRow = Math.min(getRow().getRowNumber(), squareToMoveOn.getRow().getRowNumber());
            int lastRow = Math.max(getRow().getRowNumber(), squareToMoveOn.getRow().getRowNumber());
            for (int y = firstRow + 1; y < lastRow; y++) {
                squaresOnThePath.add(Position.posToSquare(getFile(), Row.fromNumber(y)));
            }
        } else if (getRow() == squareToMoveOn.getRow()) {
            int firstFile = Math.min(getFile().getFileNumber(), squareToMoveOn.getFile().getFileNumber());
            int lastFile = Math.max(getFile().getFileNumber(), squareToMoveOn.getFile().getFileNumber());
            for (int x = firstFile + 1; x < lastFile; x++) {
                squaresOnThePath.add(Position.posToSquare(File.fromNumber(x), getRow()));
            }
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