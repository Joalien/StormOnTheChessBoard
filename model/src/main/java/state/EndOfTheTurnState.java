package state;

import card.SCCard;

public class EndOfTheTurnState implements State {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        return false;
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        return false;
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        return false;
    }

//    public void nextTurn() {
//        if (this.currentTurn == Color.WHITE) this.currentTurn = Color.BLACK;
//        else if (this.currentTurn == Color.BLACK) this.currentTurn = Color.WHITE;
//        else throw new IllegalStateException("Who's turn?");
//    }
}
