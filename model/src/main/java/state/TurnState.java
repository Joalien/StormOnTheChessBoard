package state;

import card.Card;

public interface TurnState {

    boolean tryToMove(GameStateController gameStateController, String from, String to);

    boolean tryToPlayCard(GameStateController gameStateController, Card card);

    boolean tryToPass(GameStateController gameStateController);
}
