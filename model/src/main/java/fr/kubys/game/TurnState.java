package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;

public sealed interface TurnState permits BeforeMoveCardPlayedState, BeginningOfTheTurnState, EndOfTheTurnState, MoveWithoutCardPlayedState {

    void tryToMove(GameStateController gameStateController, Position from, Position to);

    <T extends CardParam> void tryToPlayCard(GameStateController gameStateController, Card<T> card, T params);

    void tryToPass(GameStateController gameStateController);
}
