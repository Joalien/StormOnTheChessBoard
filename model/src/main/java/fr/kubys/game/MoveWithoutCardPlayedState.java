package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.core.Position;
import fr.kubys.game.exception.AlreadyMovedException;

import java.util.List;

public final class MoveWithoutCardPlayedState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new AlreadyMovedException();
    }

    @Override
    public void tryToPlayCard(GameStateController gameStateController, Card card, List<?> params) {
        if (card.getType() != CardType.AFTER_TURN) throw new IllegalStateException();
        card.playOn(gameStateController.getChessBoard(), params);
        gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
    }
}
