public class King extends Piece implements Castlable{

    //Variales d'instances
    private boolean hasMovedInThePast;
    private int positionSurLigneDeDépart;
    private int positionSurColonneDeDépart;
    private boolean cestLeRock = false;

    //Constructeur
    public King(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
        hasMovedInThePast = false;
        positionSurLigneDeDépart = x;
        positionSurColonneDeDépart = y;
    }

    //Penser à intégrer le rock !
    public boolean reachableSquares(int x, int y) {
        boolean ARetourner;
        if ((Math.abs(positionSurLigne - x) <= 1) && (Math.abs(positionSurColonne - y) <= 1)) {
            ARetourner = true;
        }

        // Kingside castling
        else if (!getRoiEnEchecs() && (positionSurLigne + 2 == x) && (positionSurColonne == y) && (!hasMovedInThePast) && ((Rock) Main.getEchiquier(8, y)).getHasMovedInThePast() == false && (positionSurLigne == positionSurLigneDeDépart) && (positionSurColonne == positionSurColonneDeDépart) && (this.RegardeSiEchecs(this.getCouleur()) == false)) {
            cestLeRock = true;
            ARetourner = true;
        }
        //Grand rock
        else if ((!getRoiEnEchecs()) && (positionSurLigne - 2 == x) && (positionSurColonne == y) && (!hasMovedInThePast) && ((Rock) Main.getEchiquier(1, y)).getHasMovedInThePast() == false && (positionSurLigne == positionSurLigneDeDépart) && (positionSurColonne == positionSurColonneDeDépart) && (this.RegardeSiEchecs(this.getCouleur()) == false)) {
            cestLeRock = true;
            ARetourner = true;
        } else {
            ARetourner = false;
        }
        return ARetourner;
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