package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

public sealed interface TurnState permits BeforeMoveCardPlayedState, BeginningOfTheTurnState, EndOfTheTurnState, MoveWithoutCardPlayedState {

    void tryToMove(GameStateController gameStateController, Position from, Position to);

    void tryToPlayCard(GameStateController gameStateController, Card card, Object params);

    void tryToPass(GameStateController gameStateController);
}
