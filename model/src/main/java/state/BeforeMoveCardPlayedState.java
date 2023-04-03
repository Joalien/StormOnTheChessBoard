package state;

import card.Card;
import state.exception.CardAlreadyPlayedException;

public class BeforeMoveCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        boolean hasMoved = gameStateController.getChessBoard().tryToMove(from, to);
        if (hasMoved) gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, Card card) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        return false;
    }
}
