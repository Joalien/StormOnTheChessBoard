package state;

import card.Card;

import java.util.List;

public interface TurnState {

    boolean tryToMove(GameStateController gameStateController, String from, String to);

    boolean tryToPlayCard(GameStateController gameStateController, Card card, List<?> params);

    boolean tryToPass(GameStateController gameStateController);
}
