package state;

import card.SCCard;
import card.SCType;
import state.exception.AlreadyMovedException;

public class MoveWithoutCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, SCCard card) {
        if (card.getType() != SCType.AFTER_TURN) throw new IllegalStateException();
        boolean hasPlayedCard = card.playOn(gameStateController.getChessBoard());
        if (hasPlayedCard) gameStateController.setState(StateEnum.END_OF_THE_TURN);
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        gameStateController.setState(StateEnum.END_OF_THE_TURN);
        return true;
    }
}
