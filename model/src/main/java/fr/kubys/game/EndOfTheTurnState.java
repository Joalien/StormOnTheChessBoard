package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;
import fr.kubys.game.exception.AlreadyMovedException;
import fr.kubys.game.exception.CardAlreadyPlayedException;

import java.util.List;

public final class EndOfTheTurnState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new AlreadyMovedException();
    }

    @Override
    public void tryToPlayCard(GameStateController gameStateController, Card card, List<?> params) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public void tryToPass(GameStateController gsc) {
    }
}
