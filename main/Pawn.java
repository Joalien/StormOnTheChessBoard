import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color, color == Color.WHITE ? 'P' : 'p');
    }

    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        boolean moveTwoSquaresFromStart;
        boolean moveOneSquare;
        boolean takePiece;
        if (getColor() == Color.WHITE) {
            moveTwoSquaresFromStart = getY() == 2 && y == 4;
            moveOneSquare = y - getY() == 1;
        } else {
            moveTwoSquaresFromStart = getY() == 7 && y == 5;
            moveOneSquare = y - getY() == -1;
        }
        takePiece = moveOneSquare && Math.abs(x - getX()) == 1;
        return (moveTwoSquaresFromStart && color.isEmpty() && x == getX())
                || (moveOneSquare && color.isEmpty() && x == getX())
                || (takePiece && color.map(c -> c != this.getColor()).orElse(false));
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (reachableSquares(squareToMoveOn, Optional.empty())) {
            if (getColor() == Color.WHITE && BoardUtil.getX(squareToMoveOn) == getX() && getY() == 2 && BoardUtil.getY(squareToMoveOn) == 4) {
                return Set.of(BoardUtil.posToSquare(getX(), 3));
            }
            if (getColor() == Color.BLACK && BoardUtil.getX(squareToMoveOn) == getX() && getY() == 7 && BoardUtil.getY(squareToMoveOn) == 5) {
                return Set.of(BoardUtil.posToSquare(getX(), 6));
            }
        }
        return Collections.emptySet();
    }

    //L'avance de 2 cases est gérée dans deplacementDeLaPiece
    public boolean nothingOnThePath(int x, int y) {
        return true;
    }


    public void testPromotion(int x, int y) {
        Piece piecePromue;
        Scanner sc = new Scanner(System.in);
        boolean validUserInput = false;
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
                        validUserInput = true;
                        piecePromue = new Queen(this.getColor());
                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
                        Controller.setEchiquier(x, y, piecePromue);
                        break;

                    case "T":
                        validUserInput = true;
                        piecePromue = new Rock(this.getColor());
                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
                        Controller.setEchiquier(x, y, piecePromue);
                        break;

                    case "F":
                        validUserInput = true;
                        piecePromue = new Bishop(this.getColor());
                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
                        Controller.setEchiquier(x, y, piecePromue);
                        break;

                    case "C":
                        validUserInput = true;
                        piecePromue = new Knight(this.getColor());
                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
                        Controller.setEchiquier(x, y, piecePromue);
                        break;

                    default: {
                        validUserInput = false;
                    }
                }
            } while (validUserInput == false);
        }
    }
}