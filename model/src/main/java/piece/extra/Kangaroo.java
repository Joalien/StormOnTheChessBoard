package piece.extra;

import piece.Color;
import piece.Knight;
import piece.Piece;
import piece.Square;
import position.File;
import position.Position;
import position.Row;

import java.util.Collections;
import java.util.Set;

public class Kangaroo extends Piece {

    public Kangaroo(Color color) {
        super(color, color == Color.WHITE ? 'N' : 'n');
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
}