package state;

import card.SCCard;
import card.SCType;
import state.exception.AlreadyMovedException;

public class MoveWithoutCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        if (card.getType() != SCType.AFTER_TURN) throw new IllegalStateException();
        boolean hasPlayedCard = card.playOn(chessBoardFacade.getChessBoard());
        if (hasPlayedCard) chessBoardFacade.setState(StateEnum.END_OF_THE_TURN);
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        chessBoardFacade.setState(StateEnum.END_OF_THE_TURN);
        return true;
    }
}
