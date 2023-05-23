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
        super(color, color == Color.WHITE ? 'R' : 'r');
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) { // FIXME avoid Optional
        if (getFile() == file && getRow() == row) return false; // fail
        return (getRow() == row) || (getFile() == file);
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (!super.isPositionTheoreticallyReachable(squareToMoveOn)) return Collections.emptySet();

        Set<Position> squaresOnThePath = new HashSet<>();
        if (getFile() == squareToMoveOn.getFile()) {
            for (int y = Math.min(getRow().getRowNumber(), squareToMoveOn.getRow().getRowNumber()) + 1; y < Math.max(getRow().getRowNumber(), squareToMoveOn.getRow().getRowNumber()); y++) {
                squaresOnThePath.add(Position.posToSquare(getFile(), Row.fromNumber(y)));
            }
        } else if (getRow() == squareToMoveOn.getRow()) {
            for (int x = Math.min(getFile().getFileNumber(), squareToMoveOn.getFile().getFileNumber()) + 1; x < Math.max(getFile().getFileNumber(), squareToMoveOn.getFile().getFileNumber()); x++) {
                squaresOnThePath.add(Position.posToSquare(File.fromNumber(x), getRow()));
            }
        }
        return squaresOnThePath;
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