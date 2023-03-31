package card;

import board.ChessBoard;
import effet.MagnetismEffect;
import piece.Piece;

public class MagnetismCard extends SCCard {

    private final Piece piece;
    public MagnetismCard(Piece piece) {
        super("", "", SCType.REPLACE_TURN);
        this.piece = piece;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (piece == null) throw new IllegalStateException();
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.addEffect(new MagnetismEffect(piece));
        return true;
    }
}
