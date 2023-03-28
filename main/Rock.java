import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Rock extends Piece implements Castlable {

    boolean ABouger;

    public Rock(Color color) {
        super(color, color == Color.WHITE ? 'R' : 'r');
        ABouger = false;
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

    //ça marche !
    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        if ((getX() == x) && (getY() < y)) {
            for (int i = getY() + 1; i < y; i++) {
                try {
                    Controller.getEchiquier(x, i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        } else if ((getX() == x) && (getY() > y)) {
            for (int i = getY() - 1; i > y; i--) {
                try {
                    Controller.getEchiquier(x, i).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else if ((getY() == y) && (getX() > x)) {
            for (int i = getX() - 1; i > x; i--) {
                try {
                    Controller.getEchiquier(i, y).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else if ((getY() == y) && (getX() < x)) {
            for (int i = getX() + 1; i < x; i++) {
                try {
                    Controller.getEchiquier(i, y).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else {
            AReturn = true;
        }
        return AReturn;
    }


    public boolean getHasMovedInThePast() {
        return ABouger;
    }

    public void setHasMovedInThePast(boolean trool) {
        ABouger = trool;
    }

    public boolean getCestLeRock() {
        return false;
    }

    public void setCestLeRock(boolean trool) {
    }
}