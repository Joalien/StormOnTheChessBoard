package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

public final class BeginningOfTheTurnState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        gameStateController.getChessBoard().tryToMove(from, to);
        gameStateController.setCurrentState(StateEnum.MOVE_WITHOUT_CARD_PLAYED);
    }

    @Override
    public void tryToPlayCard(GameStateController gameStateController, Card card, Object params) {
        StateEnum nextState = switch (card.getType()) {
            case BEFORE_TURN -> StateEnum.BEFORE_MOVE;
            case REPLACE_TURN -> StateEnum.END_OF_THE_TURN;
            default -> throw new IllegalStateException("You can only play BEFORE or REPLACE card!");
        };
        card.playOn(gameStateController.getChessBoard(), params); // FIXME
        gameStateController.setCurrentState(nextState);
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        throw new IllegalStateException("You cannot pass before playing a move");
    }
}
