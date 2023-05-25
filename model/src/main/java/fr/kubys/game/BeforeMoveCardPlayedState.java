package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;
import fr.kubys.game.exception.CardAlreadyPlayedException;

import java.util.List;

public class BeforeMoveCardPlayedState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, Position from, Position to) {
        boolean hasMoved = gameStateController.getChessBoard().tryToMove(from, to);
        if (hasMoved) gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, Card card, List<?> params) {
        throw new CardAlreadyPlayedException();
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        return false;
    }
}