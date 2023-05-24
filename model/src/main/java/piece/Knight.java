package piece;

import position.File;
import position.Position;
import position.Row;

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
        boolean distanceOfThree = Math.abs(getFile().getFileNumber() - file.getFileNumber()) + Math.abs(getRow().getRowNumber() - row.getRowNumber()) == 3;
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