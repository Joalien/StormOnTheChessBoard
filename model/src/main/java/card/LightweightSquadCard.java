package card;

import board.ChessBoard;
import piece.Pawn;

public class LightweightSquadCard extends SCCard {

    private final Pawn pawn1;
    private final Pawn pawn2;

    public LightweightSquadCard(Pawn pawn1, Pawn pawn2) {
        super("Escouade légère", "Avancez deux de vos pions, chacun de deux cases");
        this.pawn1 = pawn1;
        this.pawn2 = pawn2;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (pawn1 == null) throw new IllegalStateException();
        if (pawn2 == null) throw new IllegalStateException();
        if (pawn1.equals(pawn2)) throw new IllegalArgumentException("You should select two different pawns");
        if (pawn1.getColor() != pawn2.getColor())
            throw new IllegalArgumentException("You should move pawns of the same color");

        chessBoard.fakeSquare(null, pawn2.getPosition());
        if (cannotMoveTwoSquaresForward(chessBoard, pawn1))
            throw new IllegalArgumentException("You cannot move " + pawn1 + " two squares forward");
        chessBoard.unfakeSquare(pawn2.getPosition());
        chessBoard.fakeSquare(null, pawn1.getPosition());
        if (cannotMoveTwoSquaresForward(chessBoard, pawn2))
            throw new IllegalArgumentException("You cannot move " + pawn2 + " two squares forward");
        chessBoard.unfakeSquare(pawn1.getPosition());
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true; // FIXME
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.move(pawn1, pawn1.twoSquaresForward());
        chessBoard.move(pawn2, pawn2.twoSquaresForward());
        return true;
    }

    private boolean cannotMoveTwoSquaresForward(ChessBoard cb, Pawn pawn) {
        return cb.at(pawn.oneSquareForward()).getPiece().isPresent() || cb.at(pawn.twoSquaresForward()).getPiece().isPresent();
    }
}
