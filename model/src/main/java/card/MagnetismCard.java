package card;

import board.ChessBoard;
import effet.MagnetismEffect;
import piece.Piece;

import java.util.List;

public class MagnetismCard extends Card {

    private Piece piece;

    public MagnetismCard() {
        super("Magnetisme", "", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        piece = (Piece) params.get(0);
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (piece == null) throw new IllegalStateException();
        if (piece.getPosition() == null)
            throw new IllegalArgumentException("%s is not on the board!".formatted(piece));
        if (piece.getColor() != isPlayedBy) throw new CannotMoveThisColorException(isPlayedBy.opposite());
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
