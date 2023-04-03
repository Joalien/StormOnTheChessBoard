package state;

import card.SCCard;
import state.exception.CardAlreadyPlayedException;

public class BeforeMoveCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        boolean hasMoved = gameStateController.getChessBoard().tryToMove(from, to);
        if (hasMoved) gameStateController.setState(StateEnum.END_OF_THE_TURN);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, SCCard card) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        return false;
    }
}
