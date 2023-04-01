package state;

import card.SCCard;
import piece.Color;

public class EndOfTheTurn implements State {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        return false;
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        return false;
    }

//    public void nextTurn() {
//        if (this.currentTurn == Color.WHITE) this.currentTurn = Color.BLACK;
//        else if (this.currentTurn == Color.BLACK) this.currentTurn = Color.WHITE;
//        else throw new IllegalStateException("Who's turn?");
//    }
}
