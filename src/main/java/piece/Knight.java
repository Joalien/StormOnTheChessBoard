package piece;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class Knight extends Piece {

    public Knight(Color color) {
        super(color, color == Color.WHITE ? 'N' : 'n');
    }

    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        boolean isNotOnTheSameLine = getX() != x;
        boolean isNotOnTheSameFile = getY() != y;
        boolean distanceOfThree = Math.abs(getX() - x) + Math.abs(getY() - y) == 3;
        return isNotOnTheSameLine && isNotOnTheSameFile && distanceOfThree;
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return Collections.emptySet(); // piece.Knight can jump over pieces
    }

}