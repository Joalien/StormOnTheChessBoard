package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.game.exception.InvalidGameActionException;

public final class EnemyReactionState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new InvalidGameActionException("Cannot move during enemy reaction");
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        if (card.getType() != CardType.ENEMY_TURN) throw new InvalidGameActionException("Can only play ENEMY_TURN cards during enemy reaction");
        card.playOn(gameStateController.getChessBoard(), params);
        gameStateController.setEnemyCardPlayedThisTurn(true);
        gameStateController.setCurrentState(gameStateController.getReturnStateAfterEnemyReaction());
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        throw new InvalidGameActionException("Enemy reaction should be auto-resolved by controller");
    }
}
