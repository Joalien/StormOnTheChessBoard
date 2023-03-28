import java.util.HashSet;
import java.util.Set;

public class Queen extends Piece {
    @Deprecated
    public Queen(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
    }

    public Queen(String position, Color couleur) {
        super(new Square(position), couleur, couleur == Color.WHITE ? 'B' : 'b');
    }

    @Override
    public boolean reachableSquares(int x, int y) {
        if (this.x == x && this.y == y) return false;
        return (Math.abs(this.y - y) == Math.abs(this.x - x)) || ((this.y == y) || (this.x == x));
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.reachableSquares(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        Set<String> squaresOnThePath = new HashSet<>();
        if (this.x == BoardUtil.getX(squareToMoveOn)) {
            for (int y = Math.min(this.y, BoardUtil.getY(squareToMoveOn)) + 1; y < Math.max(this.y, BoardUtil.getY(squareToMoveOn)); y++) {
                squaresOnThePath.add(BoardUtil.posToSquare(this.x, y));
            }
        } else if (this.y == BoardUtil.getY(squareToMoveOn)) {
            for (int x = Math.min(this.x, BoardUtil.getX(squareToMoveOn)) + 1; x < Math.max(this.x, BoardUtil.getX(squareToMoveOn)); x++) {
                squaresOnThePath.add(BoardUtil.posToSquare(x, this.y));
            }
        } else {
            boolean signX = this.x < BoardUtil.getX(squareToMoveOn);
            boolean signY = this.y < BoardUtil.getY(squareToMoveOn);
            for (int i = 1; i < Math.abs(this.x - BoardUtil.getX(squareToMoveOn)); i++) {
                squaresOnThePath.add(BoardUtil.posToSquare(this.x + i * (signX ? 1 : -1), this.y + i * (signY ? 1 : -1)));
            }
        }
        return squaresOnThePath;
    }

    //ça marche !
    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        //On monte
        if ((this.x == x) && (this.y < y)) {
            for (int i = this.y + 1; i < y; i++) {
                try {
                    Main.getEchiquier(x, i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //On descend
        else if ((this.x == x) && (this.y > y)) {
            for (int i = this.y - 1; i > y; i--) {
                try {
                    Main.getEchiquier(x, i).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //On va à gauche
        else if ((this.y == y) && (this.x > x)) {
            for (int i = this.x - 1; i > x; i--) {
                try {
                    Main.getEchiquier(i, y).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //On va à droite
        else if ((this.y == y) && (this.x < x)) {
            for (int i = this.x + 1; i < x; i++) {
                try {
                    Main.getEchiquier(i, y).getColor();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //Cadran en haut à droite
        else if ((y > this.y) && (x > this.x)) {
            for (int i = 1; x - this.x > i; i++) {
                try {
                    Main.getEchiquier(this.x + i, this.y + i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en haut à gauche
        else if ((y > this.y) && (x < this.x)) {
            for (int i = 1; this.x - x > i; i++) {
                try {
                    Main.getEchiquier(this.x - i, this.y + i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à gauche
        else if ((y < this.y) && (x < this.x)) {
            for (int i = 1; this.x - x > i; i++) {
                try {
                    Main.getEchiquier(this.x - i, this.y - i).getColor();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à droite
        else if ((y < this.y) && (x > this.x)) {
            for (int i = 1; x - this.x > i; i++) {
                try {
                    Main.getEchiquier(this.x + i, this.y - i).getColor();
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