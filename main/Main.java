import java.util.Scanner;

/*Ce qu'il me reste à faire
 * empêcher le rock si échecs (j'ai l'impression qu'il ne détecte pas l'échecs à la fin
 * du tour mais bien au début du suivant ...)
 */


public class Main {

    private static Piece board[][] = new Piece[9][9];
    private static Scanner sc = new Scanner(System.in);

    public static void main(String args[]) {


        initBoard();

        boolean validMove = false;

        do {
            printBoard(board);

            System.out.println("Saisissez la pièce à déplacer et son futur emplacement");

            try {
                //Je demande la saisie complète
                String pièceADéplacer = sc.nextLine();
                //Je transforme le premier char en String pour comparer en ignorant case
                String initialePièce;

                //Si il n'y a pas d'ambiguïté ==3
                if (pièceADéplacer.length() == 3) {

                    int lettreArrivé = transformerStringEnInt(pièceADéplacer.charAt(1));
                    String csteNulle = "" + pièceADéplacer.charAt(2);
// 					int chiffreArrivé = Integer.valueOf(csteNulle).intValue();
                    int chiffreArrivé = transformerStringEnInt(pièceADéplacer.charAt(2));
                    for (int i = 0; i < board.length; i++) {
                        for (int j = 0; j < board[0].length; j++) {
                            try {
                                String typeEnChar = "" + board[i][j].getType();
                                initialePièce = "" + pièceADéplacer.charAt(0);
                                if (typeEnChar.equalsIgnoreCase(initialePièce)) {
                                    if (validMove == false) {
                                        validMove = board[i][j].allerSur(lettreArrivé, chiffreArrivé);
                                    }
                                }
                            } catch (Exception NullPointerException) {
                            }
                        }
                    }
                }
                //Si il y a une prise de pièce ==4
                else if (pièceADéplacer.length() == 4) {
                    int lettrePourDifférencier = 0;
                    int chiffrePourDifférencier = 0;
                    try {
                        lettrePourDifférencier = transformerStringEnInt(pièceADéplacer.charAt(1));
                    } catch (Exception Exception) {
                        chiffrePourDifférencier = pièceADéplacer.charAt(1);
                    }
                    int lettreArrivé = transformerStringEnInt(pièceADéplacer.charAt(2));
                    String csteNulle = "" + Integer.valueOf(transformerStringEnInt(pièceADéplacer.charAt(3))).intValue();
                    int chiffreArrivé = Integer.valueOf(csteNulle).intValue();
                    for (int i = 0; i < board.length; i++) {
                        for (int j = 0; j < board[0].length; j++) {
                            try {
                                String typeEnChar = "" + board[i][j].getType();
                                initialePièce = "" + pièceADéplacer.charAt(0);
                                if ((typeEnChar.equalsIgnoreCase(initialePièce)) && ((lettrePourDifférencier == board[i][j].getPositionSurLigne()) || (chiffrePourDifférencier == board[i][j].getPositionSurColonne()))) {
                                    if (validMove == false) {
                                        validMove = board[i][j].allerSur(lettreArrivé, chiffreArrivé);
                                    }
                                }
                            } catch (Exception NullPointerException) {
                            }
                        }
                    }
                }
                //Si un pion bouge (pour une prise, il faut préciser que c'est un pion pour else if == 4) ==2
                else if (pièceADéplacer.length() == 2) {
                    int lettreArrivé = transformerStringEnInt(pièceADéplacer.charAt(0));
                    int chiffreArrivé = transformerStringEnInt(pièceADéplacer.charAt(1));

                    for (int i = 0; i < board.length; i++) {
                        for (int j = 0; j < board[0].length; j++) {
                            try {
                                char typeEnChar = board[i][j].getType();
                                if ((typeEnChar == 'P') || (typeEnChar == 'p')) {
                                    if (validMove == false) {
                                        validMove = board[i][j].allerSur(lettreArrivé, chiffreArrivé);
                                    }
                                }
                            } catch (Exception NullPointerException) {
                            }
                        }
                    }
                } else {
                    System.out.println("Les cases indiquées n'existent pas !");
                }
                validMove = false;
            } catch (Exception InputMismatchException) {
                System.out.println("Les cases indiquées n'existent pas !");
            }

        } while (true);

    }

    private static void initBoard() {
        board[5][1] = new King(5, 1, true, 'R');
        board[5][8] = new King(5, 8, false, 'r');
        board[4][1] = new Queen(4, 1, true, 'D');
        board[4][8] = new Queen(4, 8, false, 'd');
        board[3][1] = new Bishop(3, 1, true, 'F');
        board[6][1] = new Bishop(6, 1, true, 'F');
        board[6][8] = new Bishop(6, 8, false, 'f');
        board[2][1] = new Knight(2, 1, true, 'C');
        board[3][8] = new Bishop(3, 8, false, 'f');
        board[7][1] = new Knight(7, 1, true, 'C');
        board[2][8] = new Knight(2, 8, false, 'c');
        board[7][8] = new Knight(7, 8, false, 'c');
        board[1][1] = new Rock(1, 1, true, 'T');
        board[8][1] = new Rock(8, 1, true, 'T');
        board[1][8] = new Rock(1, 8, false, 't');
        board[8][8] = new Rock(8, 8, false, 't');
        board[1][2] = new Pawn(1, 2, true, 'P');
        board[2][2] = new Pawn(2, 2, true, 'P');
        board[3][2] = new Pawn(3, 2, true, 'P');
        board[4][2] = new Pawn(4, 2, true, 'P');
        board[5][2] = new Pawn(5, 2, true, 'P');
        board[6][2] = new Pawn(6, 2, true, 'P');
        board[7][2] = new Pawn(7, 2, true, 'P');
        board[8][2] = new Pawn(8, 2, true, 'P');
        board[1][7] = new Pawn(1, 7, false, 'p');
        board[2][7] = new Pawn(2, 7, false, 'p');
        board[3][7] = new Pawn(3, 7, false, 'p');
        board[4][7] = new Pawn(4, 7, false, 'p');
        board[5][7] = new Pawn(5, 7, false, 'p');
        board[6][7] = new Pawn(6, 7, false, 'p');
        board[7][7] = new Pawn(7, 7, false, 'p');
        board[8][7] = new Pawn(8, 7, false, 'p');
        Empty empty = new Empty(0, 0, '-');
        for (int i = 3; i <= 6; i++) {
            for (int j = 1; j <= 8; j++) {
                board[j][i] = getEchiquier(0, 0);
            }
        }
    }

    public static void printBoard(Piece tab[][]) {
        char symbole = '!';
        System.out.print("\n\n\n");

        for (int i = tab.length - 1; i >= 1; i--) {
            System.out.print(i + "|      ");
            for (int j = 1; j < tab.length; j++) {
                try {
                    symbole = board[j][i].getType();
                } catch (Exception exception) {
                    symbole = '-';
                } finally {
                    System.out.print(symbole + "     ");
                }

            }
            System.out.print("                       " + (9 - i) + "          ");
            for (int j = 1; j < tab.length; j++) {
                try {
                    symbole = board[9 - j][9 - i].getType();
                } catch (Exception exception) {
                    symbole = '-';
                } finally {
                    System.out.print(symbole + "     ");
                }

            }
            System.out.print("\n\n\n");
        }
        System.out.print("      _______________________________________________                                   _______________________________________________\n");
        System.out.print("        A     B     C     D     E     F     G     H                                       H     G     F     E     D     C     B     A\n\n\n\n\n");

    }

    public static Piece getEchiquier(int x, int y) {
        return board[x][y];
    }

    public static void setEchiquier(int x, int y, Piece piece) {
        board[x][y] = piece;
    }

    // Méthode qui récupère la lettre et la transforme en chiffre
    public static int transformerStringEnInt(char sc) {
        int AReturn = 0;
        switch (sc) {
            case 'a':
                AReturn = 1;
                break;
            case 'b':
                AReturn = 2;
                break;
            case 'c':
                AReturn = 3;
                break;
            case 'd':
                AReturn = 4;
                break;
            case 'e':
                AReturn = 5;
                break;
            case 'f':
                AReturn = 6;
                break;
            case 'g':
                AReturn = 7;
                break;
            case 'h':
                AReturn = 8;
                break;

            case '&':
                AReturn = 1;
                break;
            case 'é':
                AReturn = 2;
                break;
            case '"':
                AReturn = 3;
                break;
            case '\'':
                AReturn = 4;
                break;
            case '(':
                AReturn = 5;
                break;
            case '-':
                AReturn = 6;
                break;
            case 'è':
                AReturn = 7;
                break;
            case '_':
                AReturn = 8;
                break;

            default:
                String scbis = "" + sc;
                AReturn = Integer.valueOf(scbis).intValue();
                break;
        }

        return AReturn;
    }


}