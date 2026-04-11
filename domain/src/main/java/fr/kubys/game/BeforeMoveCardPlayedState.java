package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.game.exception.CardAlreadyPlayedException;
import fr.kubys.game.exception.InvalidGameActionException;

public final class BeforeMoveCardPlayedState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        gameStateController.getChessBoard().tryToMove(from, to);
        gameStateController.transitionToState(StateEnum.END_OF_THE_TURN);
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        throw new InvalidGameActionException("Vous ne pouvez pas passer avant de jouer un coup");
    }
}
