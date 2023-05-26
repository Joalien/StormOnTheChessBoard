package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

import java.util.List;

public sealed interface TurnState permits BeforeMoveCardPlayedState, BeginningOfTheTurnState, EndOfTheTurnState, MoveWithoutCardPlayedState {

    void tryToMove(GameStateController gameStateController, Position from, Position to);

    void tryToPlayCard(GameStateController gameStateController, Card card, List<?> params);

    void tryToPass(GameStateController gameStateController);
}
