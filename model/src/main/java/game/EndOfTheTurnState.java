package game;

import card.Card;
import core.Position;
import game.exception.AlreadyMovedException;
import game.exception.CardAlreadyPlayedException;

import java.util.List;

public class EndOfTheTurnState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new AlreadyMovedException();
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, Card card, List<?> params) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public boolean tryToPass(GameStateController gsc) {
        gsc.setCurrentState(StateEnum.BEGINNING_OF_THE_TURN);
        if (gsc.getCurrentPlayer() == gsc.getWhite()) gsc.setCurrentPlayer(gsc.getBlack());
        else if (gsc.getCurrentPlayer() == gsc.getBlack()) gsc.setCurrentPlayer(gsc.getWhite());
        else throw new IllegalStateException("Who's turn?");
        return true;
    }
}