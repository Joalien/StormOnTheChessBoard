import java.util.Set;

public abstract class Piece {

    private static boolean tourDes = true;
    private static boolean roiEnEchecs = false;
    //Variales d'instances
    protected int y;
    protected int x;
    protected boolean couleur;
    protected char type;


    //Constructeur
    public Piece(int x, int y, boolean color, char typePiece) {
        this.y = y;
        this.x = x;
        couleur = color;
        type = typePiece;
    }

    public Piece(int x, int y, char typePiece) {
        this.y = y;
        this.x = x;
        type = typePiece;
    }

    //Test toutes les pièces Blanches de l"échiquier pour voir si un déplacement sur (x, y) est possible
    public static int DéplacementPossiblePièceBlanche(int x, int y) {
        int nombreDePiècesBlanchesPouvantAllerSurLaCase = 0;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                try {
                    if ((Main.getEchiquier(i, j).DeplacementPermis(x, y) == true) && (Main.getEchiquier(i, j).getCouleur() == true) && (Main.getEchiquier(i, j).nothingOnThePath(x, y) == true)) {
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
                    if ((Main.getEchiquier(i, j).DeplacementPermis(x, y) == true) && (Main.getEchiquier(i, j).getCouleur() == false) && (Main.getEchiquier(i, j).nothingOnThePath(x, y) == true)) {
                        nombreDePiècesNoiresPouvantAllerSurLaCase++;
                    }
                } catch (Exception NullPointerException) {
                }
            }
        }
// 		System.out.println("Il y a " + nombreDePiècesNoiresPouvantAllerSurLaCase +" pièce(s) noires pouvant aller sur "+x+", "+y);
        return nombreDePiècesNoiresPouvantAllerSurLaCase;

    }

    //Setters, guetter et abstract méthodes
    public boolean getRoiEnEchecs() {
        return roiEnEchecs;
    }

    public void setRoiEnEchecs(boolean trool) {
        roiEnEchecs = trool;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean getCouleur() {
        return couleur;
    }

    public char getType() {
        return this.type;
    }

    protected abstract boolean reachableSquares(int x, int y);

    protected boolean reachableSquares(String s) {
        return reachableSquares(BoardUtil.getX(s), BoardUtil.getY(s));
    }

    public abstract Set<String> squaresOnThePath(String squareToMoveOn);

    @Deprecated
    public abstract boolean nothingOnThePath(int x, int y);

    @Deprecated
    protected boolean nothingOnThePath(String s) {
        return nothingOnThePath(BoardUtil.getX(s), BoardUtil.getY(s));
    }

    //Vérifie si le déplacement est permis (ne prend pas en compte la couleur de la case d'arrivée, et si le chemin est libre et s'il n'y a pas d'échecs)
    protected boolean DeplacementPermis(int x, int y) {
        boolean deplacementPermis;

        if ((ResteDansEchiquier(x, y) == true) && (reachableSquares(x, y) == true) && (bonTour() == true)) {
            deplacementPermis = true;
        } else {
            deplacementPermis = false;
        }
        return deplacementPermis;
    }

    //Case d'arrivée vide
    protected boolean RegardeSiCaseVide(int x, int y) {
        if (Main.getEchiquier(x, y) == Main.getEchiquier(0, 0)) {
            return true;
        } else {
            return false;
        }
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
            if (this.getCouleur() == tourDes) {
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
    public boolean allerSur(int x, int y) {
        boolean memeColor;
        boolean AReturn;
        //Empêche une pièce de prendre une pièce de sa propre couleur
        try {
            if (this.getCouleur() == Main.getEchiquier(x, y).getCouleur()) {
                memeColor = true;
            } else {
                memeColor = false;
            }
        } catch (Exception NullPointerException) {
            memeColor = false;
        }

        //Si la pièce bouge (sauf si ça entraine un échecs)
        if ((DeplacementPermis(x, y) == true) && (memeColor == false) && (nothingOnThePath(x, y) == true)) {
            //sauvegarde la position de départ
            int yy = this.y;
            int xx = this.x;

            //déplace l'objet
            this.y = y;
            this.x = x;
            //Sauvegarde la pièce mangée (1, 0) au cas où le coup soit impossible et qu'il faille revenir en arrière
            Main.setEchiquier(1, 0, Main.getEchiquier(x, y));
            Main.setEchiquier(x, y, Main.getEchiquier(xx, yy));
            Main.setEchiquier(xx, yy, Main.getEchiquier(0, 0));

            //Passe au tour suivant
            tourDes = !tourDes;

            //Vérifie si le coup ne met pas le roi en échecs
            boolean regardeSiEchecs = RegardeSiEchecs(this.getCouleur());
            if (regardeSiEchecs == true) {
                System.out.println("Le déplacement n'est pas possible car il mettrait le roi en échecs !");
                //Remet les pièces à leur place
                this.y = yy;
                this.x = xx;
                Main.setEchiquier(xx, yy, Main.getEchiquier(x, y));
                Main.setEchiquier(x, y, Main.getEchiquier(1, 0));
                //Revient au tour d'avant
                tourDes = !tourDes;
                return false;
            }
            //Indique que la pièce a bougée
            else {
                if (this instanceof Castlable)
                    ((Castlable) this).setHasMovedInThePast(true);
                AReturn = true;
            }
            //Test promotion et rock (déplace la tour si le roi se déplace de deux cases)
            if (this instanceof Pawn) ((Pawn) this).testPromotion(x, y);
            //Petit Rock
            if (this instanceof Castlable && ((Castlable) this).getCestLeRock() == true && (x == 7)) {
                System.out.println("Petit rock !");
                //déplace l'objet
                yy = Main.getEchiquier(8, y).y;
                xx = Main.getEchiquier(8, y).x;
                Main.getEchiquier(8, y).y = y;
                Main.getEchiquier(8, y).x = 6;
                Main.setEchiquier(6, y, Main.getEchiquier(xx, yy));
                Main.setEchiquier(xx, yy, Main.getEchiquier(0, 0));
                ((Castlable) this).setCestLeRock(false);
            }
            //Grand Rock
            else if (this instanceof Castlable && ((Castlable) this).getCestLeRock() == true && (x == 3)) {
                System.out.println("Grand rock !");
                //déplace l'objet
                yy = Main.getEchiquier(1, y).y;
                xx = Main.getEchiquier(1, y).x;
                Main.getEchiquier(1, y).y = y;
                Main.getEchiquier(1, y).x = 3;
                Main.setEchiquier(4, y, Main.getEchiquier(xx, yy));
                Main.setEchiquier(xx, yy, Main.getEchiquier(0, 0));
                ((Castlable) this).setCestLeRock(false);
            }

            //Regarde si le coup met le roi adverse en échecs
            roiEnEchecs = false;
            if (RegardeSiEchecs(!this.getCouleur()) == true) {
                System.out.println("échecs !");
                roiEnEchecs = true;
            }

            AReturn = true;
        } else {
            AReturn = false;
        }
        return AReturn;

    }

    //Renvoie true si pièce blanche, false si noire
    protected boolean RegardeSiPieceBlanche(int x, int y) {
        boolean ARetourner = false;
        try {
            if ((Main.getEchiquier(x, y).getCouleur() == true)) {
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
                    if ((Main.getEchiquier(i, j).getType() == 'R') && (couleurDeLaPièce == true) && (DéplacementPossiblePièceNoire(i, j) > 0)) {
                        System.out.println("échecs !");
                        return true;
                    } else if ((Main.getEchiquier(i, j).getType() == 'R') && (couleurDeLaPièce == false) && (DéplacementPossiblePièceNoire(i, j) > 0)) {
                        System.out.println("échecs !");
                        return false;
                    }

                    if ((Main.getEchiquier(i, j).getType() == 'r') && (couleurDeLaPièce == false) && (DéplacementPossiblePièceBlanche(i, j) > 0)) {
                        System.out.println("échecs !");
                        return true;
                    } else if ((Main.getEchiquier(i, j).getType() == 'r') && (couleurDeLaPièce == true) && (DéplacementPossiblePièceBlanche(i, j) > 0)) {
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