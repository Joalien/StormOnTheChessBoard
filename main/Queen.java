import java.util.Set;

public class Queen extends Piece {

    //Variales d'instances

    //Constructeur
    public Queen(int x, int y, boolean color, char lettre) {

        //Caractéristiques du constructeur, variables d'instances
        super(x, y, color, lettre);
    }

    @Override
    public boolean reachableSquares(int x, int y) {
        boolean ARetourner;
        if ((Math.abs(this.y - y) == Math.abs(this.x - x)) || ((this.y == y) || (this.x == x))) {
            ARetourner = true;
        } else {
            ARetourner = false;
        }
        return ARetourner;
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return null;
    }

    //ça marche !
    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        //On monte
        if ((this.x == x) && (this.y < y)) {
            for (int i = this.y + 1; i < y; i++) {
                try {
                    Main.getEchiquier(x, i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //On descend
        else if ((this.x == x) && (this.y > y)) {
            for (int i = this.y - 1; i > y; i--) {
                try {
                    Main.getEchiquier(x, i).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //On va à gauche
        else if ((this.y == y) && (this.x > x)) {
            for (int i = this.x - 1; i > x; i--) {
                try {
                    Main.getEchiquier(i, y).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //On va à droite
        else if ((this.y == y) && (this.x < x)) {
            for (int i = this.x + 1; i < x; i++) {
                try {
                    Main.getEchiquier(i, y).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //Cadran en haut à droite
        else if ((y > this.y) && (x > this.x)) {
            for (int i = 1; x - this.x > i; i++) {
                try {
                    Main.getEchiquier(this.x + i, this.y + i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en haut à gauche
        else if ((y > this.y) && (x < this.x)) {
            for (int i = 1; this.x - x > i; i++) {
                try {
                    Main.getEchiquier(this.x - i, this.y + i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à gauche
        else if ((y < this.y) && (x < this.x)) {
            for (int i = 1; this.x - x > i; i++) {
                try {
                    Main.getEchiquier(this.x - i, this.y - i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à droite
        else if ((y < this.y) && (x > this.x)) {
            for (int i = 1; x - this.x > i; i++) {
                try {
                    Main.getEchiquier(this.x + i, this.y - i).getCouleur();
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