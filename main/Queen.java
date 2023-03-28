import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color, color == Color.WHITE ? 'B' : 'b');
    }

    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        if (getX() == x && getY() == y) return false;
        return (Math.abs(getY() - y) == Math.abs(getX() - x)) || ((getY() == y) || (getX() == x));
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.reachableSquares(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        Set<String> squaresOnThePath = new HashSet<>();
        if (getX() == BoardUtil.getX(squareToMoveOn)) {
            for (int y = Math.min(getY(), BoardUtil.getY(squareToMoveOn)) + 1; y < Math.max(getY(), BoardUtil.getY(squareToMoveOn)); y++) {
                squaresOnThePath.add(BoardUtil.posToSquare(getX(), y));
            }
        } else if (getY() == BoardUtil.getY(squareToMoveOn)) {
            for (int x = Math.min(getX(), BoardUtil.getX(squareToMoveOn)) + 1; x < Math.max(getX(), BoardUtil.getX(squareToMoveOn)); x++) {
                squaresOnThePath.add(BoardUtil.posToSquare(x, getY()));
            }
        } else {
            boolean signX = getX() < BoardUtil.getX(squareToMoveOn);
            boolean signY = getY() < BoardUtil.getY(squareToMoveOn);
            for (int i = 1; i < Math.abs(getX() - BoardUtil.getX(squareToMoveOn)); i++) {
                squaresOnThePath.add(BoardUtil.posToSquare(getX() + i * (signX ? 1 : -1), getY() + i * (signY ? 1 : -1)));
            }
        }
        return squaresOnThePath;
    }


}