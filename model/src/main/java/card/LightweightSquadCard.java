package card;

import board.ChessBoard;
import piece.Pawn;

import java.util.List;

public class LightweightSquadCard extends Card {

    private Pawn pawn1;
    private Pawn pawn2;

    public LightweightSquadCard() {
        super("Escouade légère", "Avancez deux de vos pions, chacun de deux cases", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this. pawn1 = (Pawn) params.get(0);
        this. pawn2 = (Pawn) params.get(1);
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
            throw new IllegalArgumentException("You cannot move %s two squares forward".formatted(pawn1));
        chessBoard.unfakeSquare(pawn2.getPosition());
        chessBoard.fakeSquare(null, pawn1.getPosition());
        if (cannotMoveTwoSquaresForward(chessBoard, pawn2))
            throw new IllegalArgumentException("You cannot move %s two squares forward".formatted(pawn2));
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
