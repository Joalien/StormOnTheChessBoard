public class Bishop extends Piece {

    public Bishop(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
    }


    public boolean reachableSquares(int x, int y) {
        if (this.positionSurLigne == x && this.positionSurColonne == y) return false;
        return Math.abs(this.positionSurColonne - y) == Math.abs(this.positionSurLigne - x);
    }

    //ça marche !
    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        //Cadran en haut à droite
        if ((y > positionSurColonne) && (x > positionSurLigne)) {
            for (int i = 1; x - positionSurLigne > i; i++) {
                try {
                    Main.getEchiquier(positionSurLigne + i, positionSurColonne + i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en haut à gauche
        else if ((y > positionSurColonne) && (x < positionSurLigne)) {
            for (int i = 1; positionSurLigne - x > i; i++) {
                try {
                    Main.getEchiquier(positionSurLigne - i, positionSurColonne + i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à gauche
        else if ((y < positionSurColonne) && (x < positionSurLigne)) {
            for (int i = 1; positionSurLigne - x > i; i++) {
                try {
                    Main.getEchiquier(positionSurLigne - i, positionSurColonne - i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //Cadran en bas à droite
        else if ((y < positionSurColonne) && (x > positionSurLigne)) {
            for (int i = 1; x - positionSurLigne > i; i++) {
                try {
                    Main.getEchiquier(positionSurLigne + i, positionSurColonne - i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        } else {
            AReturn = true;
        }
        return AReturn;
    }




   

    

    

    public boolean getCestLeRock() {
        return false;
    }

    public void setCestLeRock(boolean trool) {
    }


}