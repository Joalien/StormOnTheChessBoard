import java.util.Optional;
import java.util.Set;

public abstract class Piece {

    private static boolean tourDes = true;
    private static boolean roiEnEchecs = false;
    //Variales d'instances
    protected Color color;
    protected char type;
    private Square square;

    public String getPosition() {
        return this.getSquare().map(Square::getPosition).orElse(null);
    }

    public void isTaken() {
        this.square = null;
    }

    public Piece(Square square, Color color, char typePiece) {
        this.square = square;
        this.color = color;
        this.type = typePiece;
    }

    public Piece(Color color, char typePiece) {
        this.color = color;
        this.type = typePiece;
    }

    //Test toutes les pièces Blanches de l"échiquier pour voir si un déplacement sur (x, y) est possible
    public static int DéplacementPossiblePièceBlanche(int x, int y) {
        int nombreDePiècesBlanchesPouvantAllerSurLaCase = 0;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                try {
                    if ((Controller.getEchiquier(i, j).DeplacementPermis(x, y) == true) && (Controller.getEchiquier(i, j).getColor() == Color.WHITE) && (Controller.getEchiquier(i, j).nothingOnThePath(x, y) == true)) {
                        nombreDePiècesBlanchesPouvantAllerSurLaCase++;
                    }
                } catch (Exception NullPointerException) {
                }
            }
        }
// 		System.out.println("Il y a " + nombreDePiècesBlanchesPouvantAllerSurLaCase +" pièce(s) blanches pouvant aller sur "+x+", "+y);
        return nombreDePiècesBlanchesPouvantAllerSurLaCase;

    }

    //Test toutes les pièces Noires de l"échiquier pour voir si un déplacement sur (x, y) est possible
    public static int DéplacementPossiblePièceNoire(int x, int y) {
        int nombreDePiècesNoiresPouvantAllerSurLaCase = 0;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                try {
                    if ((Controller.getEchiquier(i, j).DeplacementPermis(x, y) == true) && (Controller.getEchiquier(i, j).getColor() == Color.BLACK) && (Controller.getEchiquier(i, j).nothingOnThePath(x, y) == true)) {
                        nombreDePiècesNoiresPouvantAllerSurLaCase++;
                    }
                } catch (Exception NullPointerException) {
                }
            }
        }
// 		System.out.println("Il y a " + nombreDePiècesNoiresPouvantAllerSurLaCase +" pièce(s) noires pouvant aller sur "+x+", "+y);
        return nombreDePiècesNoiresPouvantAllerSurLaCase;

    }

    public Optional<Square> getSquare() {
        return Optional.ofNullable(square);
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    //Setters, guetter et abstract méthodes
    public boolean getRoiEnEchecs() {
        return roiEnEchecs;
    }

    public void setRoiEnEchecs(boolean trool) {
        roiEnEchecs = trool;
    }

    public int getY() {
        return BoardUtil.getY(square.getPosition());
    }

    public int getX() {
        return BoardUtil.getX(square.getPosition());
    }


    public Color getColor() {
        return color;
    }

    public char getType() {
        return this.type;
    }

    protected abstract boolean reachableSquares(int x, int y, Optional<Color> color);

    protected boolean reachableSquares(String s) {
        return reachableSquares(BoardUtil.getX(s), BoardUtil.getY(s), Optional.empty());
    }

    protected boolean reachableSquares(String s, Optional<Color> color) {
        return reachableSquares(BoardUtil.getX(s), BoardUtil.getY(s), color);
    }

    public abstract Set<String> squaresOnThePath(String squareToMoveOn);

    @Deprecated
    public abstract boolean nothingOnThePath(int x, int y);

    //Vérifie si le déplacement est permis (ne prend pas en compte la couleur de la case d'arrivée, et si le chemin est libre et s'il n'y a pas d'échecs)
    protected boolean DeplacementPermis(int x, int y) {
        return (ResteDansEchiquier(x, y) == true) && (reachableSquares(x, y, Optional.empty()) == true) && (bonTour() == true);
    }

    //Vérifie que la pièce reste bien dans l'échiquier
    protected boolean ResteDansEchiquier(int x, int y) {
        if ((x >= 1) && (x <= 8) && (y >= 1) && (y <= 8)) {
            return true;
        } else {
            return false;
        }
    }

    //Vérifie si le joueur joue bien les pièces de sa couleur
    private boolean bonTour() {
        try {
            if (this.getColor() == Color.WHITE == tourDes) {
                return true;
            } else {
// 				System.out.println("Vous ne pouvez pas jouer cette couleur");
                return false;
            }
        } catch (Exception NullPointerException) {
            System.out.println("Vous ne pouvez déplacer qu'une case déjà occupée");
            return false;
        }
    }

    //méthode principale qui permet de déplacer la pièce
//    public boolean allerSur(int x, int y) {
//        boolean memeColor;
//        boolean AReturn;
//        //Empêche une pièce de prendre une pièce de sa propre couleur
//        try {
//            if (this.getColor() == Controller.getEchiquier(x, y).getColor()) {
//                memeColor = true;
//            } else {
//                memeColor = false;
//            }
//        } catch (Exception NullPointerException) {
//            memeColor = false;
//        }
//
//        //Si la pièce bouge (sauf si ça entraine un échecs)
//        if ((DeplacementPermis(x, y) == true) && (memeColor == false) && (nothingOnThePath(x, y) == true)) {
//            //sauvegarde la position de départ
//            int yy = getY();
//            int xx = getX();
//
//            //déplace l'objet
//            getY() = y;
//            getX() = x;
//            //Sauvegarde la pièce mangée (1, 0) au cas où le coup soit impossible et qu'il faille revenir en arrière
//            Controller.setEchiquier(1, 0, Controller.getEchiquier(x, y));
//            Controller.setEchiquier(x, y, Controller.getEchiquier(xx, yy));
//            Controller.setEchiquier(xx, yy, Controller.getEchiquier(0, 0));
//
//            //Passe au tour suivant
//            tourDes = !tourDes;
//
//            //Vérifie si le coup ne met pas le roi en échecs
//            boolean regardeSiEchecs = RegardeSiEchecs(this.getColor() == Color.WHITE);
//            if (regardeSiEchecs == true) {
//                System.out.println("Le déplacement n'est pas possible car il mettrait le roi en échecs !");
//                //Remet les pièces à leur place
//                getY() = yy;
//                getX() = xx;
//                Controller.setEchiquier(xx, yy, Controller.getEchiquier(x, y));
//                Controller.setEchiquier(x, y, Controller.getEchiquier(1, 0));
//                //Revient au tour d'avant
//                tourDes = !tourDes;
//                return false;
//            }
//            //Indique que la pièce a bougée
//            else {
//                if (this instanceof Castlable)
//                    ((Castlable) this).setHasMovedInThePast(true);
//                AReturn = true;
//            }
//            //Test promotion et rock (déplace la tour si le roi se déplace de deux cases)
//            if (this instanceof Pawn) ((Pawn) this).testPromotion(x, y);
//            //Petit Rock
//            if (this instanceof Castlable && ((Castlable) this).getCestLeRock() == true && (x == 7)) {
//                System.out.println("Petit rock !");
//                //déplace l'objet
//                yy = Controller.getEchiquier(8, y).y;
//                xx = Controller.getEchiquier(8, y).x;
//                Controller.getEchiquier(8, y).y = y;
//                Controller.getEchiquier(8, y).x = 6;
//                Controller.setEchiquier(6, y, Controller.getEchiquier(xx, yy));
//                Controller.setEchiquier(xx, yy, Controller.getEchiquier(0, 0));
//                ((Castlable) this).setCestLeRock(false);
//            }
//            //Grand Rock
//            else if (this instanceof Castlable && ((Castlable) this).getCestLeRock() == true && (x == 3)) {
//                System.out.println("Grand rock !");
//                //déplace l'objet
//                yy = Controller.getEchiquier(1, y).y;
//                xx = Controller.getEchiquier(1, y).x;
//                Controller.getEchiquier(1, y).y = y;
//                Controller.getEchiquier(1, y).x = 3;
//                Controller.setEchiquier(4, y, Controller.getEchiquier(xx, yy));
//                Controller.setEchiquier(xx, yy, Controller.getEchiquier(0, 0));
//                ((Castlable) this).setCestLeRock(false);
//            }
//
//            //Regarde si le coup met le roi adverse en échecs
//            roiEnEchecs = false;
//            if (RegardeSiEchecs(this.getColor() == Color.WHITE) == true) {
//                System.out.println("échecs !");
//                roiEnEchecs = true;
//            }
//
//            AReturn = true;
//        } else {
//            AReturn = false;
//        }
//        return AReturn;
//
//    }

    //Renvoie true si pièce blanche, false si noire
    protected boolean RegardeSiPieceBlanche(int x, int y) {
        boolean ARetourner = false;
        try {
            if ((Controller.getEchiquier(x, y).getColor() == Color.WHITE)) {
                ARetourner = true;
            } else {
                ARetourner = false;
            }
        } catch (Exception NullPointerException) {
        }

        return ARetourner;
    }

    //Repère les échecs
    protected boolean RegardeSiEchecs(boolean couleurDeLaPièce) {
        boolean AReturn = false;
        //trouvons où se cachent les rois ...
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                try {
                    if ((Controller.getEchiquier(i, j).getType() == 'R') && (couleurDeLaPièce == true) && (DéplacementPossiblePièceNoire(i, j) > 0)) {
                        System.out.println("échecs !");
                        return true;
                    } else if ((Controller.getEchiquier(i, j).getType() == 'R') && (couleurDeLaPièce == false) && (DéplacementPossiblePièceNoire(i, j) > 0)) {
                        System.out.println("échecs !");
                        return false;
                    }

                    if ((Controller.getEchiquier(i, j).getType() == 'r') && (couleurDeLaPièce == false) && (DéplacementPossiblePièceBlanche(i, j) > 0)) {
                        System.out.println("échecs !");
                        return true;
                    } else if ((Controller.getEchiquier(i, j).getType() == 'r') && (couleurDeLaPièce == true) && (DéplacementPossiblePièceBlanche(i, j) > 0)) {
                        System.out.println("échecs !");
                        return false;
                    }
                } catch (Exception Exception) {
                }
            }
        }
        return AReturn;
    }

}