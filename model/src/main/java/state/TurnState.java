package state;

import card.Card;
import position.Position;

import java.util.List;

public interface TurnState {

    boolean tryToMove(GameStateController gameStateController, Position from, Position to);

    boolean tryToPlayCard(GameStateController gameStateController, Card card, List<?> params);

    boolean tryToPass(GameStateController gameStateController);
}
