package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.game.exception.AlreadyMovedException;
import fr.kubys.game.exception.CardAlreadyPlayedException;

public final class EndOfTheTurnState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new AlreadyMovedException();
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public void tryToPass(GameStateController gsc) {
    }
}
