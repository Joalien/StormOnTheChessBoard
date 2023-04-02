package state;

import card.SCCard;
import state.exception.AlreadyMovedException;
import state.exception.CardAlreadyPlayedException;

public class MoveWithCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        chessBoardFacade.setState(StateEnum.END_OF_THE_TURN);
        return true;
    }
}
