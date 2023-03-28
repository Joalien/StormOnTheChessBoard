import java.util.HashSet;
import java.util.Set;

public class Bishop extends Piece {

    public Bishop(String position, Color color) {
        super(new Square(position), color, color == Color.WHITE ? 'B' : 'b');
    }

    @Override
    public boolean reachableSquares(int x, int y) {
        if (this.x == x && this.y == y) return false;
        return Math.abs(this.y - y) == Math.abs(this.x - x);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.reachableSquares(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        Set<String> squaresOnThePath = new HashSet<>();
        boolean signX = this.x < BoardUtil.getX(squareToMoveOn);
        boolean signY = this.y < BoardUtil.getY(squareToMoveOn);
        for (int i = 1; i < Math.abs(this.x - BoardUtil.getX(squareToMoveOn)); i++) {
            squaresOnThePath.add(BoardUtil.posToSquare(this.x + i * (signX ? 1 : -1), this.y + i * (signY ? 1 : -1)));
        }
        return squaresOnThePath;
    }

    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        //Cadran en haut à droite
        if ((y > this.y) && (x > this.x)) {
            for (int i = 1; x - this.x > i; i++) {
                try {
                    Controller.getEchiquier(this.x + i, this.y + i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en haut à gauche
        else if ((y > this.y) && (x < this.x)) {
            for (int i = 1; this.x - x > i; i++) {
                try {
                    Controller.getEchiquier(this.x - i, this.y + i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à gauche
        else if ((y < this.y) && (x < this.x)) {
            for (int i = 1; this.x - x > i; i++) {
                try {
                    Controller.getEchiquier(this.x - i, this.y - i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à droite
        else if ((y < this.y) && (x > this.x)) {
            for (int i = 1; x - this.x > i; i++) {
                try {
                    Controller.getEchiquier(this.x + i, this.y - i).getColor();
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