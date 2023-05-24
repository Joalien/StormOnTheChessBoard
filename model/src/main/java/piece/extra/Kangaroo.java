package piece.extra;

import core.Color;
import piece.Knight;
import piece.Piece;
import piece.Square;
import core.File;
import core.Position;
import core.Row;

import java.util.Collections;
import java.util.Set;

public class Kangaroo extends Piece {

    public Kangaroo(Color color) {
        super(color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        return Position.generateAllPositions().stream()
                .map(pos -> {
                    Knight knight = new Knight(this.color);
                    knight.setSquare(new Square(pos));
                    return knight;
                }).filter(knight -> knight.isPositionTheoreticallyReachable(file, row, color))
                .anyMatch(knight -> knight.isPositionTheoreticallyReachable(getFile(), getRow(), color));
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