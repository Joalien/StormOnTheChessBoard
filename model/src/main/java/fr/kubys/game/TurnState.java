package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

import java.util.List;

public interface TurnState {

    boolean tryToMove(GameStateController gameStateController, Position from, Position to);

    boolean tryToPlayCard(GameStateController gameStateController, Card card, List<?> params);

    boolean tryToPass(GameStateController gameStateController);
}
