package piece;

import position.PositionUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Rock extends Piece implements Castlable {

    private boolean canCastle = true;

    public Rock(Color color) {
        super(color, color == Color.WHITE ? 'R' : 'r');
    }

    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        if (getX() == x && getY() == y) return false;
        return (getY() == y) || (getX() == x);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.reachableSquares(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        Set<String> squaresOnThePath = new HashSet<>();
        if (getX() == PositionUtil.getX(squareToMoveOn)) {
            for (int y = Math.min(getY(), PositionUtil.getY(squareToMoveOn)) + 1; y < Math.max(getY(), PositionUtil.getY(squareToMoveOn)); y++) {
                squaresOnThePath.add(PositionUtil.posToSquare(getX(), y));
            }
        } else {
            for (int x = Math.min(getX(), PositionUtil.getX(squareToMoveOn)) + 1; x < Math.max(getX(), PositionUtil.getX(squareToMoveOn)); x++) {
                squaresOnThePath.add(PositionUtil.posToSquare(x, getY()));
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