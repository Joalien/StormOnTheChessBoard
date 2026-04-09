package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;

public final class PromotionPendingState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new IllegalStateException("Cannot move while a promotion is pending");
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        throw new IllegalStateException("Cannot play a card while a promotion is pending");
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        // Accepting default promotion (Queen already placed by auto-promotion)
        gameStateController.clearPendingPromotions();
    }
}
