import java.util.HashSet;
import java.util.Set;

public class Rock extends Piece implements Castlable {

    boolean ABouger;

    public Rock(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
        ABouger = false;
    }

    public Rock(String position, Color couleur) {
        super(new Square(position), couleur, couleur == Color.WHITE ? 'R' : 'r');
        ABouger = false;
    }

    @Override
    public boolean reachableSquares(int x, int y) {
        if (this.x == x && this.y == y) return false;
        return (this.y == y) || (this.x == x);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.reachableSquares(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        Set<String> squaresOnThePath = new HashSet<>();
        if (this.x == BoardUtil.getX(squareToMoveOn)) {
            for (int y = Math.min(this.y, BoardUtil.getY(squareToMoveOn)) + 1; y < Math.max(this.y, BoardUtil.getY(squareToMoveOn)); y++) {
                squaresOnThePath.add(BoardUtil.posToSquare(this.x, y));
            }
        } else {
            for (int x = Math.min(this.x, BoardUtil.getX(squareToMoveOn)) + 1; x < Math.max(this.x, BoardUtil.getX(squareToMoveOn)); x++) {
                squaresOnThePath.add(BoardUtil.posToSquare(x, this.y));
            }
        }
        return squaresOnThePath;
    }

    //ça marche !
    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        if ((this.x == x) && (this.y < y)) {
            for (int i = this.y + 1; i < y; i++) {
                try {
                    Main.getEchiquier(x, i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        } else if ((this.x == x) && (this.y > y)) {
            for (int i = this.y - 1; i > y; i--) {
                try {
                    Main.getEchiquier(x, i).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else if ((this.y == y) && (this.x > x)) {
            for (int i = this.x - 1; i > x; i--) {
                try {
                    Main.getEchiquier(i, y).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else if ((this.y == y) && (this.x < x)) {
            for (int i = this.x + 1; i < x; i++) {
                try {
                    Main.getEchiquier(i, y).getColor();
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