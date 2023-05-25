package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

import java.util.List;

public interface TurnState {

    void tryToMove(GameStateController gameStateController, Position from, Position to);

    void tryToPlayCard(GameStateController gameStateController, Card card, List<?> params);

    void tryToPass(GameStateController gameStateController);
}
