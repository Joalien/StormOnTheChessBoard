package state;

import card.SCCard;
import state.exception.AlreadyMovedException;
import state.exception.CardAlreadyPlayedException;

public class EndOfTheTurnState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, SCCard card) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public boolean tryToPass(GameStateController cb) {
        cb.setState(StateEnum.BEGINNING_OF_THE_TURN);
        if (cb.getCurrentPlayer() == cb.getWhite()) cb.setCurrentPlayer(cb.getBlack());
        else if (cb.getCurrentPlayer() == cb.getBlack()) cb.setCurrentPlayer(cb.getWhite());
        else throw new IllegalStateException("Who's turn?");
        return true;
    }

}
