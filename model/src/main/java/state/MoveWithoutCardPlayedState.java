package state;

import card.Card;
import card.CardType;
import state.exception.AlreadyMovedException;

import java.util.List;

public class MoveWithoutCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, Card card, List<?> params) {
        if (card.getType() != CardType.AFTER_TURN) throw new IllegalStateException();
        boolean hasPlayedCard = card.playOn(gameStateController.getChessBoard(), params);
        if (hasPlayedCard) gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        return true;
    }
}
