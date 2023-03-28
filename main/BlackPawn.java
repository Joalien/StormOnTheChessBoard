import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class BlackPawn extends Pawn {

    public BlackPawn() {
        super(Color.BLACK, 'p');
    }

    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        boolean moveTwoSquaresFromStart = getY() == 7 && y == 5;
        boolean moveOneSquare = y - getY() == -1;
        boolean moveForward = color.isEmpty() && x == getX() && (moveTwoSquaresFromStart || moveOneSquare);

        boolean takePiece = moveOneSquare && Math.abs(x - getX()) == 1;
        boolean takeBlackPiece = color.map(c -> c == Color.WHITE).orElse(false) && takePiece;

        return moveForward || takeBlackPiece;
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        boolean moveForwardTwoSquaresFromStart = reachableSquares(squareToMoveOn, Optional.empty())
                && getY() == 7
                && BoardUtil.getY(squareToMoveOn) == 5;
        return moveForwardTwoSquaresFromStart ? Set.of(BoardUtil.posToSquare(getX(), 6)) : Collections.emptySet();
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