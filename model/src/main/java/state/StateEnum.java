package state;

import card.SCCard;
import lombok.Getter;

@Getter
public enum StateEnum implements TurnState {
    BEGINNING_OF_THE_TURN(new BeginningOfTheTurnState()),
    BEFORE_MOVE(new BeforeMoveCardPlayedState()),
    MOVE_WITHOUT_CARD_PLAYED(new MoveWithoutCardPlayedState()),
    END_OF_THE_TURN(new EndOfTheTurnState());

    private final TurnState turnState;

    StateEnum(TurnState turnState) {
        this.turnState = turnState;
    }

    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        return this.turnState.tryToMove(gameStateController, from, to);
    }

    public boolean tryToPlayCard(GameStateController gameStateController, SCCard card) {
        return this.turnState.tryToPlayCard(gameStateController, card);
    }

    public boolean tryToPass(GameStateController gameStateController) {
        return this.turnState.tryToPass(gameStateController);
    }
}
