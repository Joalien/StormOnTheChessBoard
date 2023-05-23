package piece;

import position.File;
import position.Position;
import position.Row;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color, color == Color.WHITE ? 'B' : 'b');
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Optional<Color> color) { // FIXME Optional as parameter
        if (getFile() == file && getRow() == row) return false;
        return (Math.abs(getRow().getRowNumber() - row.getRowNumber()) == Math.abs(getFile().getFileNumber() - file.getFileNumber())) || ((getRow() == row) || (getFile() == file)); // FIXME clean code
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
        } else {
            boolean signX = getFile().getFileNumber() < squareToMoveOn.getFile().getFileNumber();
            boolean signY = getRow().getRowNumber() < squareToMoveOn.getRow().getRowNumber();
            for (int i = 1; i < Math.abs(getFile().getFileNumber() - squareToMoveOn.getFile().getFileNumber()); i++) {
                squaresOnThePath.add(Position.posToSquare(File.fromNumber(getFile().getFileNumber() + i * (signX ? 1 : -1)), Row.fromNumber(getRow().getRowNumber() + i * (signY ? 1 : -1))));
            }
        }
        return squaresOnThePath;
    }


}