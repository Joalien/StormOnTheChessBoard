package state;

import card.SCCard;

public class EndOfTheTurnState implements TurnState {
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

//    public void nextMove() {
//        if (this.currentMove == Color.WHITE) this.currentMove = Color.BLACK;
//        else if (this.currentMove == Color.BLACK) this.currentMove = Color.WHITE;
//        else throw new IllegalStateException("Who's turn?");
//    }
}
