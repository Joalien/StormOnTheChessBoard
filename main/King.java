import java.util.Set;

public class King extends Piece implements Castlable {

    //Variales d'instances
    private boolean hasMovedInThePast;
    private boolean cestLeRock = false;

    //Constructeur
    public King(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
        hasMovedInThePast = false;
    }

    //Penser à intégrer le rock !
    @Override
    public boolean reachableSquares(int x, int y) {
        boolean ARetourner;
        if ((Math.abs(this.x - x) <= 1) && (Math.abs(this.y - y) <= 1)) {
            ARetourner = true;
        }

        // Kingside castling
        else if (!getRoiEnEchecs() && (this.x + 2 == x) && (this.y == y) && (!hasMovedInThePast) && ((Rock) Main.getEchiquier(8, y)).getHasMovedInThePast() == false && (this.RegardeSiEchecs(this.getColor() == Color.WHITE) == false)) {
            cestLeRock = true;
            ARetourner = true;
        }
        //Grand rock
        else if ((!getRoiEnEchecs()) && (this.x - 2 == x) && (this.y == y) && (!hasMovedInThePast) && ((Rock) Main.getEchiquier(1, y)).getHasMovedInThePast() == false && (this.RegardeSiEchecs(this.getColor() == Color.WHITE) == false)) {
            cestLeRock = true;
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

    public boolean getCestLeRock() {
        return cestLeRock;
    }

    public void setCestLeRock(boolean encoreLeRock) {
        cestLeRock = encoreLeRock;
    }


    public boolean getHasMovedInThePast() {
        return hasMovedInThePast;
    }

    public void setHasMovedInThePast(boolean trool) {
        hasMovedInThePast = trool;
    }

    public boolean nothingOnThePath(int x, int y) {
        return true;
    }


}