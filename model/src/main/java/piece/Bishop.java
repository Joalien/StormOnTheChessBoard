package piece;

import position.File;
import position.Position;
import position.Row;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color, color == Color.WHITE ? 'B' : 'b');
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Optional<Color> color) {
        if (getFile() == file && getRow() == row) return false;
        return Math.abs(getRow().getRowNumber() - row.getRowNumber()) == Math.abs(getFile().getFileNumber() - file.getFileNumber());
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (!super.isPositionTheoreticallyReachable(squareToMoveOn)) return Collections.emptySet();

        Set<Position> squaresOnThePath = new HashSet<>();
        boolean signX = getFile().getFileNumber() < squareToMoveOn.getFile().getFileNumber();
        boolean signY = getRow().getRowNumber() < squareToMoveOn.getRow().getRowNumber();
        for (int i = 1; i < Math.abs(getFile().getFileNumber() - squareToMoveOn.getFile().getFileNumber()); i++) {
            squaresOnThePath.add(Position.posToSquare(getFile().getFileNumber() + i * (signX ? 1 : -1), getRow().getRowNumber() + i * (signY ? 1 : -1)));
        }
        return squaresOnThePath;
    }

}