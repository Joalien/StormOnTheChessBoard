package piece;

import position.PositionUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color, color == Color.WHITE ? 'B' : 'b');
    }

    @Override
    public boolean isPositionTheoricallyReachable(int x, int y, Optional<Color> color) {
        if (getX() == x && getY() == y) return false;
        return (Math.abs(getY() - y) == Math.abs(getX() - x)) || ((getY() == y) || (getX() == x));
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.isPositionTheoricallyReachable(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        Set<String> squaresOnThePath = new HashSet<>();
        if (getX() == PositionUtil.getX(squareToMoveOn)) {
            for (int y = Math.min(getY(), PositionUtil.getY(squareToMoveOn)) + 1; y < Math.max(getY(), PositionUtil.getY(squareToMoveOn)); y++) {
                squaresOnThePath.add(PositionUtil.posToSquare(getX(), y));
            }
        } else if (getY() == PositionUtil.getY(squareToMoveOn)) {
            for (int x = Math.min(getX(), PositionUtil.getX(squareToMoveOn)) + 1; x < Math.max(getX(), PositionUtil.getX(squareToMoveOn)); x++) {
                squaresOnThePath.add(PositionUtil.posToSquare(x, getY()));
            }
        } else {
            boolean signX = getX() < PositionUtil.getX(squareToMoveOn);
            boolean signY = getY() < PositionUtil.getY(squareToMoveOn);
            for (int i = 1; i < Math.abs(getX() - PositionUtil.getX(squareToMoveOn)); i++) {
                squaresOnThePath.add(PositionUtil.posToSquare(getX() + i * (signX ? 1 : -1), getY() + i * (signY ? 1 : -1)));
            }
        }
        return squaresOnThePath;
    }


}