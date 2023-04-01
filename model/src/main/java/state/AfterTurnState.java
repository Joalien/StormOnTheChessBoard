package state;

import card.SCCard;

public class AfterTurnState implements State {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        return false;
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        return false;
    }

    @Override
    public boolean tryToPass() {
        return false;
    }
}
