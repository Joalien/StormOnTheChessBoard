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
        if (getX() == BoardUtil.getX(squareToMoveOn)) {
            for (int y = Math.min(getY(), BoardUtil.getY(squareToMoveOn)) + 1; y < Math.max(getY(), BoardUtil.getY(squareToMoveOn)); y++) {
                squaresOnThePath.add(BoardUtil.posToSquare(getX(), y));
            }
        } else {
            for (int x = Math.min(getX(), BoardUtil.getX(squareToMoveOn)) + 1; x < Math.max(getX(), BoardUtil.getX(squareToMoveOn)); x++) {
                squaresOnThePath.add(BoardUtil.posToSquare(x, getY()));
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