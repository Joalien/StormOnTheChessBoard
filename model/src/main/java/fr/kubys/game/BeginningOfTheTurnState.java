package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

import java.util.List;

public class BeginningOfTheTurnState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, Position from, Position to) {
        boolean hasMoved = gameStateController.getChessBoard().tryToMove(from, to);
        if (hasMoved) gameStateController.setCurrentState(StateEnum.MOVE_WITHOUT_CARD_PLAYED);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, Card card, List<?> params) {
        StateEnum nextState = switch (card.getType()) {
            case BEFORE_TURN -> StateEnum.BEFORE_MOVE;
            case REPLACE_TURN -> StateEnum.END_OF_THE_TURN;
            default -> throw new IllegalStateException("You can only play BEFORE or REPLACE card!");
        };
        boolean hasPlayedCard = card.playOn(gameStateController.getChessBoard(), params);
        if (hasPlayedCard) {
            gameStateController.setCurrentState(nextState);
        }
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        return false;
    }
}
