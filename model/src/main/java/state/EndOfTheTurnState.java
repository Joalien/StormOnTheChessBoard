package state;

import card.SCCard;
import state.exception.AlreadyMovedException;
import state.exception.CardAlreadyPlayedException;

public class EndOfTheTurnState implements TurnState {
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
        chessBoardFacade.setState(StateEnum.BEGINNING_OF_THE_TURN);
        if (chessBoardFacade.getCurrentPlayer() == chessBoardFacade.getWhite()) chessBoardFacade.setCurrentPlayer(chessBoardFacade.getBlack());
        else if (chessBoardFacade.getCurrentPlayer() == chessBoardFacade.getBlack()) chessBoardFacade.setCurrentPlayer(chessBoardFacade.getWhite());
        else throw new IllegalStateException("Who's turn?");
        return true;
    }

}
