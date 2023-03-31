package piece;

import position.PositionUtil;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class Kangaroo extends Piece {

    public Kangaroo(Color color) {
        super(color, color == Color.WHITE ? 'N' : 'n');
    }

    @Override
    public boolean isPositionTheoreticallyReachable(int x, int y, Optional<Color> color) {
        return PositionUtil.generateAllPositions().stream()
                .map(pos -> {
                    Knight knight = new Knight(this.color);
                    knight.setSquare(new Square(pos));
                    return knight;
                }).filter(knight -> knight.isPositionTheoreticallyReachable(x, y, color))
                .anyMatch(knight -> knight.isPositionTheoreticallyReachable(getX(), getY(), color));
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return Collections.emptySet(); // piece.Knight can jump over pieces
    }
}