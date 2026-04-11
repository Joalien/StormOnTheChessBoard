package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.game.exception.InvalidGameActionException;

public final class EnemyReactionState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new InvalidGameActionException("Impossible de se déplacer pendant la réaction adverse");
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        if (card.getType() != CardType.ENEMY_TURN) throw new InvalidGameActionException("Seules les cartes TOUR ADVERSE peuvent être jouées pendant la réaction adverse");
        card.playOn(gameStateController.getChessBoard(), params);
        gameStateController.setEnemyCardPlayedThisTurn(true);
        gameStateController.setCurrentState(gameStateController.getReturnStateAfterEnemyReaction());
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        throw new InvalidGameActionException("La réaction adverse devrait être résolue automatiquement");
    }
}
