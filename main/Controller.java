import java.util.Scanner;

/*Ce qu'il me reste à faire
 * empêcher le rock si échecs (j'ai l'impression qu'il ne détecte pas l'échecs à la fin
 * du tour mais bien au début du suivant ...)
 */


public class Controller {

    private static ChessBoard chessBoard = ChessBoard.createWithInitialState();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String args[]) {

        boolean validMove = false;

        do {
            printBoard(chessBoard.getBoard());

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
                    for (int i = 0; i < chessBoard.getBoard().length; i++) {
                        for (int j = 0; j < chessBoard.getBoard()[0].length; j++) {
                            try {
                                String typeEnChar = "" + chessBoard.getBoard()[i][j].getType();
                                initialePièce = "" + pièceADéplacer.charAt(0);
                                if (typeEnChar.equalsIgnoreCase(initialePièce)) {
                                    if (validMove == false) {
//                                        validMove = chessBoard.getBoard()[i][j].allerSur(lettreArrivé, chiffreArrivé);
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
                    for (int i = 0; i < chessBoard.getBoard().length; i++) {
                        for (int j = 0; j < chessBoard.getBoard()[0].length; j++) {
                            try {
                                String typeEnChar = "" + chessBoard.getBoard()[i][j].getType();
                                initialePièce = "" + pièceADéplacer.charAt(0);
                                if ((typeEnChar.equalsIgnoreCase(initialePièce)) && ((lettrePourDifférencier == chessBoard.getBoard()[i][j].getX()) || (chiffrePourDifférencier == chessBoard.getBoard()[i][j].getY()))) {
                                    if (validMove == false) {
//                                        validMove = chessBoard.getBoard()[i][j].allerSur(lettreArrivé, chiffreArrivé);
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

                    for (int i = 0; i < chessBoard.getBoard().length; i++) {
                        for (int j = 0; j < chessBoard.getBoard()[0].length; j++) {
                            try {
                                char typeEnChar = chessBoard.getBoard()[i][j].getType();
                                if ((typeEnChar == 'P') || (typeEnChar == 'p')) {
                                    if (validMove == false) {
//                                        validMove = chessBoard.getBoard()[i][j].allerSur(lettreArrivé, chiffreArrivé);
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

    public static void printBoard(Piece tab[][]) {
        char symbole = '!';
        System.out.print("\n\n\n");

        for (int i = tab.length - 1; i >= 1; i--) {
            System.out.print(i + "|      ");
            for (int j = 1; j < tab.length; j++) {
                try {
                    symbole = chessBoard.getBoard()[j][i].getType();
                } catch (Exception exception) {
                    symbole = '-';
                } finally {
                    System.out.print(symbole + "     ");
                }

            }
            System.out.print("                       " + (9 - i) + "          ");
            for (int j = 1; j < tab.length; j++) {
                try {
                    symbole = chessBoard.getBoard()[9 - j][9 - i].getType();
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

    @Deprecated
    public static Piece getEchiquier(int x, int y) {
        return chessBoard.getBoard()[x][y];
    }

    @Deprecated
    public static void setEchiquier(int x, int y, Piece piece) {
        chessBoard.getBoard()[x][y] = piece;
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