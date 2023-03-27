import java.util.Collections;
import java.util.Set;

public class Knight extends Piece {

    public Knight(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
    }

    @Override
    public boolean reachableSquares(int x, int y) {
        boolean isNotOnTheSameLine = this.x != x;
        boolean isNotOnTheSameColumn = this.y != y;
        boolean distanceOfThree = Math.abs(this.x - x) + Math.abs(this.y - y) == 3;
        return isNotOnTheSameLine && isNotOnTheSameColumn && distanceOfThree;
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return Collections.emptySet();
    }

    //easy !!!!!
    public boolean nothingOnThePath(int x, int y) {
        return true;
    }
}