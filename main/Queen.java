public class Queen extends Piece {

    //Variales d'instances

    //Constructeur
    public Queen(int x, int y, boolean color, char lettre) {

        //Caractéristiques du constructeur, variables d'instances
        super(x, y, color, lettre);
    }

    public boolean reachableSquares(int x, int y) {
        boolean ARetourner;
        if ((Math.abs(this.positionSurColonne - y) == Math.abs(this.positionSurLigne - x)) || ((positionSurColonne == y) || (positionSurLigne == x))) {
            ARetourner = true;
        } else {
            ARetourner = false;
        }
        return ARetourner;
    }

    //ça marche !
    public boolean nothingOnThePath(int x, int y) {
        boolean AReturn = true;
        //On monte
        if ((positionSurLigne == x) && (positionSurColonne < y)) {
            for (int i = positionSurColonne + 1; i < y; i++) {
                try {
                    Main.getEchiquier(x, i).getCouleur();
                    AReturn = false;
                } catch (Exception NullPointerException) {
                }
            }
        }
        //On descend
        else if ((positionSurLigne == x) && (positionSurColonne > y)) {
            for (int i = positionSurColonne - 1; i > y; i--) {
                try {
                    Main.getEchiquier(x, i).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //On va à gauche
        else if ((positionSurColonne == y) && (positionSurLigne > x)) {
            for (int i = positionSurLigne - 1; i > x; i--) {
                try {
                    Main.getEchiquier(i, y).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //On va à droite
        else if ((positionSurColonne == y) && (positionSurLigne < x)) {
            for (int i = positionSurLigne + 1; i < x; i++) {
                try {
                    Main.getEchiquier(i, y).getCouleur();
                    AReturn = false;
                } catch (Exception Exception) {
                }
            }
        }
        //Cadran en haut à droite
        else if ((y > positionSurColonne) && (x > positionSurLigne)) {
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




   

    

    
}