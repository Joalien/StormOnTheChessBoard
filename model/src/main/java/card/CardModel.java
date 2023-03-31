package card;

import board.ChessBoard;
import piece.Color;
import piece.Piece;

public class CardModel extends SCCard {

    private String position;
    public CardModel() {
        super("", "", SCType.REPLACE_TURN);
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (position == null) throw new IllegalStateException();
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        return true;
    }
}
