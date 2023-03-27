import java.util.Set;

public class Empty extends Piece {

    public Empty(int x, int y, char lettre) {
        super(x, y, lettre);
    }

    public Empty(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
    }

    public char getType() {
        return type;
    }


    @Override
    public boolean reachableSquares(int x, int y) {
        return false;
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return null;
    }

    public boolean nothingOnThePath(int x, int y) {
        return false;
    }


}