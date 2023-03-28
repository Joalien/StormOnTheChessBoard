import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color, color == Color.WHITE ? 'B' : 'b');
    }

    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        if (getX() == x && getY() == y) return false;
        return Math.abs(getY() - y) == Math.abs(getX() - x);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.reachableSquares(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        Set<String> squaresOnThePath = new HashSet<>();
        boolean signX = getX() < BoardUtil.getX(squareToMoveOn);
        boolean signY = getY() < BoardUtil.getY(squareToMoveOn);
        for (int i = 1; i < Math.abs(getX() - BoardUtil.getX(squareToMoveOn)); i++) {
            squaresOnThePath.add(BoardUtil.posToSquare(getX() + i * (signX ? 1 : -1), getY() + i * (signY ? 1 : -1)));
        }
        return squaresOnThePath;
    }

    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        //Cadran en haut à droite
        if ((y > getY()) && (x > getX())) {
            for (int i = 1; x - getX() > i; i++) {
                try {
                    Controller.getEchiquier(getX() + i, getY() + i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en haut à gauche
        else if ((y > getY()) && (x < getX())) {
            for (int i = 1; getX() - x > i; i++) {
                try {
                    Controller.getEchiquier(getX() - i, getY() + i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à gauche
        else if ((y < getY()) && (x < getX())) {
            for (int i = 1; getX() - x > i; i++) {
                try {
                    Controller.getEchiquier(getX() - i, getY() - i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à droite
        else if ((y < getY()) && (x > getX())) {
            for (int i = 1; x - getX() > i; i++) {
                try {
                    Controller.getEchiquier(getX() + i, getY() - i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        } else {
            AReturn = true;
        }
        return AReturn;
    }
}