package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.game.exception.InvalidGameActionException;

public final class PromotionPendingState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new InvalidGameActionException("Impossible de se déplacer pendant une promotion en attente");
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        throw new InvalidGameActionException("Impossible de jouer une carte pendant une promotion en attente");
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        // Accepting default promotion (Queen already placed by auto-promotion)
        gameStateController.clearPendingPromotions();
    }
}
