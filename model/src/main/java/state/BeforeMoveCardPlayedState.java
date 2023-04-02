package state;

import card.SCCard;
import state.exception.CardAlreadyPlayedException;

public class BeforeMoveCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        boolean hasMoved = chessBoardFacade.getChessBoard().tryToMove(from, to);
        if (hasMoved) chessBoardFacade.setState(StateEnum.END_OF_THE_TURN);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        return false;
    }
}
