package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.game.exception.InvalidGameActionException;

public final class BeginningOfTheTurnState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        gameStateController.getChessBoard().tryToMove(from, to);
        gameStateController.transitionToState(StateEnum.MOVE_WITHOUT_CARD_PLAYED);
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        StateEnum nextState = switch (card.getType()) {
            case BEFORE_TURN -> StateEnum.BEFORE_MOVE;
            case REPLACE_TURN -> StateEnum.END_OF_THE_TURN;
            default -> throw new InvalidGameActionException("Vous ne pouvez jouer qu'une carte AVANT ou REMPLACE le coup !");
        };
        card.playOn(gameStateController.getChessBoard(), params); // FIXME
        gameStateController.transitionToState(nextState);
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
        throw new InvalidGameActionException("Vous ne pouvez pas passer avant de jouer un coup");
    }
}
