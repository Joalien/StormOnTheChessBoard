package state;

import card.SCCard;

public interface TurnState {

    boolean tryToMove(GameStateController gameStateController, String from, String to);

    boolean tryToPlayCard(GameStateController gameStateController, SCCard card);

    boolean tryToPass(GameStateController gameStateController);
}
