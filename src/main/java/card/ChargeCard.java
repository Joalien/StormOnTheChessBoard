package card;

import board.ChessBoard;
import position.Square;
import piece.Pawn;
import piece.Piece;

import java.util.Optional;
import java.util.Set;

public class ChargeCard extends SCCard {

    private final Set<Pawn> pawns;

    public ChargeCard(Set<Pawn> pawns) {
        super("Avancez tous ceux de vos ppions que vous voulez, et qui le peuvent, d'une case");
        this.pawns = pawns;
    }


    @Override
    public boolean play(ChessBoard chessBoard) {
        checkAttribute();

        pawns.stream()
            .peek(pawn -> fakeOtherPawns(chessBoard, pawn))
            .map(ChessBoard::oneSquaresForward)
            .map(chessBoard::at)
            .map(Square::getPiece)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .peek(ChargeCard::throwsCannotMoveOneSquareForwardException)
            .forEach(pos -> unfakeAllPawns(chessBoard));

        pawns.forEach(p -> chessBoard.move(p, ChessBoard.oneSquaresForward(p)));
        return true;
    }

    private void fakeOtherPawns(ChessBoard cb, Pawn pawn) {
        pawns.stream()
            .filter(pawn1 -> !pawn1.equals(pawn))
            .forEach(p -> cb.fakeSquare(p.getPosition(), null));
    }

    private void unfakeAllPawns(ChessBoard cb) {
        pawns.stream()
                .map(Piece::getPosition)
                .forEach(cb::unfakeSquare);
    }

    private static void throwsCannotMoveOneSquareForwardException(Piece p) {
        throw new IllegalArgumentException(p + " cannot move one square forward!");
    }

    private void checkAttribute() {
        if (pawns == null) throw new IllegalStateException();
        if (pawns.isEmpty()) throw new IllegalArgumentException("You should select at least one pawn");
    }
}
