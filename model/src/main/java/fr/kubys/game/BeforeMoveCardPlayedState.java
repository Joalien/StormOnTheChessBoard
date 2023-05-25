package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;
import fr.kubys.game.exception.CardAlreadyPlayedException;

import java.util.List;

public class BeforeMoveCardPlayedState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        gameStateController.getChessBoard().tryToMove(from, to);
        gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
    }

    @Override
    public void tryToPlayCard(GameStateController gameStateController, Card card, List<?> params) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        throw new IllegalStateException("You cannot pass before playing a move");
    }
}
