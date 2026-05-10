package fr.kubys.game;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.game.exception.AlreadyMovedException;
import fr.kubys.game.exception.InvalidGameActionException;

import java.util.ArrayList;

public final class MoveWithoutCardPlayedState implements TurnState {
    @Override
    public void tryToMove(GameStateController gameStateController, Position from, Position to) {
        throw new AlreadyMovedException();
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params) {
        if (card.getType() != CardType.AFTER_TURN) throw new InvalidGameActionException("Vous ne pouvez jouer qu'une carte APRÈS le coup");
        ChessBoard chessBoard = gameStateController.getChessBoard();
        card.playOn(chessBoard, params);
        new ArrayList<>(chessBoard.getEffects())
                .forEach(effect -> effect.afterCardPlayHook(chessBoard, card.getType()));
        gameStateController.transitionToState(StateEnum.END_OF_THE_TURN);
    }

    @Override
    public void tryToPass(GameStateController gameStateController) {
    }
}
