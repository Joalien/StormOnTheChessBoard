import java.util.Scanner;

public class Pawn extends Piece {

    public Pawn(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
    }

    //Vérifie aussi que lorsqu'il avance de 2 cases, il n'y a pas de pièce au milieu
    public boolean reachableSquares(int x, int y) {
        //Si pion blanc
        if (this.getCouleur() == true) {
            //Si case d'arrivée vide
            try {
                //Si case d'arrivée vide
                if (Main.getEchiquier(x, y).getType() == '-') {
                    //Avance normale
                    if ((positionSurLigne == x) && (positionSurColonne + 1 == y)) {
                        return true;

                    }
                    //Avance 2 cases
                    else if ((positionSurColonne == 2) && (positionSurLigne == x) && (y == 4)) {
                        //Vérifie que la case du milieu n'est pas occupée
                        try {
                            Main.getEchiquier(x, 3).getCouleur();
                            return false;
                        } catch (Exception NullPointerException) {
                            return true;
                        }
                    }
                    //Sinon
                    else {
                        return false;
                    }
                }
                //Si on effectue une prise
                else {
                    //On vérifie si la case est à gauche
                    if ((positionSurColonne + 1 == y) && positionSurLigne - 1 == x) {
                        return true;
                    }
                    //Puis à droite
                    else if ((positionSurColonne + 1 == y) && positionSurLigne + 1 == x) {
                        return true;
                    }
                    //Sinon
                    else {
                        return false;
                    }
                }
            } catch (Exception InputMismatchException) {
                //Avance normale
                if ((positionSurLigne == x) && (positionSurColonne + 1 == y)) {
                    return true;
                }
                //Avance 2 cases
                else if ((positionSurColonne == 2) && (positionSurLigne == x) && (y == 4)) {
                    //Vérifie que la case du milieu n'est pas occupée
                    try {
                        Main.getEchiquier(x, 3).getCouleur();
                        return false;
                    } catch (Exception NullPointerException) {
                        return true;
                    }
                }
                //Sinon
                else {
                    return false;
                }
            }
        }
        //Si pion noir
        else if (this.getCouleur() == false) {
            //Si case d'arrivée vide
            try {
                //Si case d'arrivée vide
                if (Main.getEchiquier(x, y).getType() == '-') {
                    //Avance normale
                    if ((positionSurLigne == x) && (positionSurColonne - 1 == y)) {
                        return true;
                    }
                    //Avance 2 cases
                    else if ((positionSurColonne == 7) && (positionSurLigne == x) && (y == 5)) {
                        //Vérifie que la case du milieu n'est pas occupée
                        try {
                            Main.getEchiquier(x, 6).getCouleur();
                            return false;
                        } catch (Exception NullPointerException) {
                            return true;
                        }
                    }
                    //Sinon
                    else {
                        return false;
                    }
                }

                //Si on effectue une prise
                else {
                    //On vérifie si la case est à gauche
                    if ((positionSurColonne - 1 == y) && positionSurLigne - 1 == x) {
                        return true;
                    }
                    //Puis à droite
                    else if ((positionSurColonne - 1 == y) && positionSurLigne + 1 == x) {
                        return true;
                    }
                    //Sinon
                    else {
                        return false;
                    }
                }
            } catch (Exception InputMismatchException) {
                //Avance normale
                if ((positionSurLigne == x) && (positionSurColonne - 1 == y)) {
                    return true;
                }
                //Avance 2 cases
                else if ((positionSurColonne == 7) && (positionSurLigne == x) && (y == 5)) {
                    //Vérifie que la case du milieu n'est pas occupée
                    try {
                        Main.getEchiquier(x, 6).getCouleur();
                        return false;
                    } catch (Exception NullPointerException) {
                        return true;
                    }
                }
                //Sinon
                else {
                    return false;
                }
            }
        } else {
            System.out.println("Ce pion n'as pas de couleur, on ne devrait pas voir cette phrase");
            return false;
        }
    }

    //L'avance de 2 cases est gérée dans deplacementDeLaPiece
    public boolean nothingOnThePath(int x, int y) {
        return true;
    }



   

    

    public void testPromotion(int x, int y) {
        Piece piecePromue;
        Scanner sc = new Scanner(System.in);
        boolean conditionSineQuaNon = false;
        //Vrifie si la pion est sur la dernière ligne
        if ((y == 1) || (y == 8)) {
            //Il y a donc une promotion !
            System.out.println("Promotion !");
            do {
                System.out.println("Quelle promotion voulez-vous choisir ? (D/T/F/C)");
                String promotion = sc.nextLine();
                //4 possibilités de promotion
                switch (promotion) {
                    case "D":
                        conditionSineQuaNon = true;
                        if (this.getCouleur() == true) {
                            piecePromue = new Queen(x, y, true, 'D');
                        } else {
                            piecePromue = new Queen(x, y, false, 'd');
                        }
                        Main.setEchiquier(x, y, piecePromue);
                        break;

                    case "T":
                        conditionSineQuaNon = true;
                        if (this.getCouleur() == true) {
                            piecePromue = new Rock(x, y, true, 'T');
                        } else {
                            piecePromue = new Rock(x, y, false, 't');
                        }
                        Main.setEchiquier(x, y, piecePromue);
                        break;

                    case "F":
                        conditionSineQuaNon = true;
                        if (this.getCouleur() == true) {
                            piecePromue = new Bishop(x, y, true, 'F');
                        } else {
                            piecePromue = new Bishop(x, y, false, 'f');
                        }
                        Main.setEchiquier(x, y, piecePromue);
                        break;

                    case "C":
                        conditionSineQuaNon = true;
                        if (this.getCouleur() == true) {
                            piecePromue = new Knight(x, y, true, 'C');
                        } else {
                            piecePromue = new Knight(x, y, false, 'c');
                        }
                        Main.setEchiquier(x, y, piecePromue);
                        break;

                    default: {
                        conditionSineQuaNon = false;
                    }
                }
            } while (conditionSineQuaNon == false);
        }
    }
}