package card;

import board.CheckException;
import board.ChessBoard;
import piece.Pawn;
import piece.Piece;
import position.Square;

import java.util.Optional;
import java.util.Set;

public class ChargeCard extends SCCard {

    private final Set<Pawn> pawns;

    public ChargeCard(Set<Pawn> pawns) {
        super("Charge", "Avancez tous ceux de vos ppions que vous voulez, et qui le peuvent, d'une case");
        this.pawns = pawns;
    }

    @Override
    public void validInput(ChessBoard chessBoard) {
        if (pawns == null) throw new IllegalStateException();
        if (pawns.isEmpty()) throw new IllegalArgumentException("You should select at least one pawn");

        pawns.stream()
                .peek(pawn -> fakeOtherPawns(chessBoard, pawn))
                .map(ChessBoard::oneSquaresForward)
                .map(chessBoard::at)
                .peek(pos -> unfakeAllPawns(chessBoard))
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(ChargeCard::throwsCannotMoveOneSquareForwardException);
    }

    @Override
    protected void doesNotCreateCheck(ChessBoard chessBoard) throws CheckException {

        pawns.forEach(p -> chessBoard.fakeSquare(p.getPosition(), null));
        pawns.forEach(p -> chessBoard.fakeSquare(ChessBoard.oneSquaresForward(p), p));

        if (chessBoard.isKingUnderAttack(pawns.stream().findAny().get().getColor())) throw new CheckException();

        chessBoard.unfakeAllSquares();
    }

    @Override
    public boolean doAction(ChessBoard chessBoard) {
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
}
