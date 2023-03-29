package card;

import board.ChessBoard;

public abstract class SCCard {

    protected String description;

    public SCCard(String description) {
        this.description = description;
    }

    public abstract boolean play(ChessBoard chessBoard);
}
