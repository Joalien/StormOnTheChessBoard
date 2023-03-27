import java.util.Set;

public class Rock extends Piece implements Castlable {

    //Variales d'instances
    boolean ABouger;

    //Constructeur
    public Rock(int x, int y, boolean color, char lettre) {

        //Caractéristiques du constructeur, variables d'instances
        super(x, y, color, lettre);
        ABouger = false;
    }

    @Override
    public boolean reachableSquares(int x, int y) {
        boolean ARetourner;
        if ((this.y == y) || (this.x == x)) {
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
        if ((this.x == x) && (this.y < y)) {
            for (int i = this.y + 1; i < y; i++) {
                try {
                    Main.getEchiquier(x, i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        } else if ((this.x == x) && (this.y > y)) {
            for (int i = this.y - 1; i > y; i--) {
                try {
                    Main.getEchiquier(x, i).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else if ((this.y == y) && (this.x > x)) {
            for (int i = this.x - 1; i > x; i--) {
                try {
                    Main.getEchiquier(i, y).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else if ((this.y == y) && (this.x < x)) {
            for (int i = this.x + 1; i < x; i++) {
                try {
                    Main.getEchiquier(i, y).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        } else {
            AReturn = true;
        }
        return AReturn;
    }


    public void setAbouger() {
        ABouger = true;
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