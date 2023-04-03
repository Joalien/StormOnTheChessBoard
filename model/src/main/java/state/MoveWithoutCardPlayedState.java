package state;

import card.Card;
import card.SCType;
import state.exception.AlreadyMovedException;

public class MoveWithoutCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, Card card) {
        if (card.getType() != SCType.AFTER_TURN) throw new IllegalStateException();
        boolean hasPlayedCard = card.playOn(gameStateController.getChessBoard());
        if (hasPlayedCard) gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        return true;
    }
}
