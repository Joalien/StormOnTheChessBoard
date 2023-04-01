package state;

import card.SCCard;

public class ReplaceTurnState implements State {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        throw new AlreadyPlayedACardException();
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        chessBoardFacade.setState(StateEnum.END_OF_THE_TURN);
        return true;
    }
}
