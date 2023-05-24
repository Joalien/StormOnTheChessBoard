package piece;

import position.File;
import position.Position;
import position.Row;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class BlackPawn extends Pawn {

    public BlackPawn() {
        super(Color.BLACK);
    }

    @Override
    public Optional<Position> twoSquaresForward() {
        return this.getRow().previous()
                .flatMap(Row::previous)
                .map(row -> Position.posToSquare(this.getFile(), row));
    }

    @Override
    public Optional<Position> oneSquareForward() {
        return this.getRow().previous()
                .map(row -> Position.posToSquare(this.getFile(), row));
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        boolean moveTwoSquaresFromStart = getRow() == Row.Seven && row == Row.Five;
        boolean moveOneSquare = getRow().previous().map(r -> r == row).orElse(false);
        boolean moveForward = color == null && file == getFile() && (moveTwoSquaresFromStart || moveOneSquare);

        boolean takePiece = moveOneSquare && Math.abs(file.getFileNumber() - getFile().getFileNumber()) == 1;
        boolean takeBlackPiece =  color == Color.WHITE && takePiece;

        return moveForward || takeBlackPiece;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        boolean moveForwardTwoSquaresFromStart = isPositionTheoreticallyReachable(squareToMoveOn)
                && getRow() == Row.Seven
                && squareToMoveOn.getRow() == Row.Five;
        return moveForwardTwoSquaresFromStart ? Set.of(Position.posToSquare(getFile(), Row.Six)) : Collections.emptySet();
    }

    @Override
    public boolean isKing() {
        return false;
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