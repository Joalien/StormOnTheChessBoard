package piece;

import position.File;
import position.Position;
import position.Row;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class BlackPawn extends Pawn {

    public BlackPawn() {
        super(Color.BLACK, 'p');
    }

    @Override
    public Position twoSquaresForward() {
        return Position.posToSquare(this.getFile(), this.getRow().previous().flatMap(Row::previous).orElseThrow());// FIXME
    }

    @Override
    public Position oneSquareForward() {
        return Position.posToSquare(this.getFile(),  this.getRow().previous().orElseThrow());
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Optional<Color> color) {
        boolean moveTwoSquaresFromStart = getRow() == Row.Seven && row == Row.Five;
        boolean moveOneSquare = getRow().previous().map(r -> r == row).orElse(false); // FIXME
        boolean moveForward = color.isEmpty() && file == getFile() && (moveTwoSquaresFromStart || moveOneSquare);

        boolean takePiece = moveOneSquare && Math.abs(file.getFileNumber() - getFile().getFileNumber()) == 1;
        boolean takeBlackPiece = color.map(c -> c == Color.WHITE).orElse(false) && takePiece;

        return moveForward || takeBlackPiece;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        boolean moveForwardTwoSquaresFromStart = isPositionTheoreticallyReachable(squareToMoveOn, Optional.empty())
                && getRow() == Row.Seven
                && squareToMoveOn.getRow() == Row.Five;
        return moveForwardTwoSquaresFromStart ? Set.of(Position.posToSquare(getFile(), Row.Six)) : Collections.emptySet();
    }


//    public void testPromotion(int x, int y) {
//        Piece piecePromue;
//        Scanner sc = new Scanner(System.in);
//        boolean validUserInput = false;
//        //Vrifie si la pion est sur la dernière ligne
//        if ((y == 1) || (y == 8)) {
//            //Il y a donc une promotion !
//            System.out.println("Promotion !");
//            do {
//                System.out.println("Quelle promotion voulez-vous choisir ? (D/T/F/C)");
//                String promotion = sc.nextLine();
//                //4 possibilités de promotion
//                switch (promotion) {
//                    case "D":
//                        validUserInput = true;
//                        piecePromue = new Queen(this.getColor());
//                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
//                        Controller.setEchiquier(x, y, piecePromue);
//                        break;
//
//                    case "T":
//                        validUserInput = true;
//                        piecePromue = new Rock(this.getColor());
//                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
//                        Controller.setEchiquier(x, y, piecePromue);
//                        break;
//
//                    case "F":
//                        validUserInput = true;
//                        piecePromue = new Bishop(this.getColor());
//                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
//                        Controller.setEchiquier(x, y, piecePromue);
//                        break;
//
//                    case "C":
//                        validUserInput = true;
//                        piecePromue = new Knight(this.getColor());
//                        piecePromue.setSquare(new Square(BoardUtil.posToSquare(x, y)));
//                        Controller.setEchiquier(x, y, piecePromue);
//                        break;
//
//                    default: {
//                        validUserInput = false;
//                    }
//                }
//            } while (validUserInput == false);
//        }
//    }
}