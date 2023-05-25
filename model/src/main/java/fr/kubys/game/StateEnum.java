package fr.kubys.game;

import lombok.Getter;

@Getter
public enum StateEnum {
    BEGINNING_OF_THE_TURN(new BeginningOfTheTurnState()),
    BEFORE_MOVE(new BeforeMoveCardPlayedState()),
    MOVE_WITHOUT_CARD_PLAYED(new MoveWithoutCardPlayedState()),
    END_OF_THE_TURN(new EndOfTheTurnState());

    private final TurnState state;

    StateEnum(TurnState state) {
        this.state = state;
    }
}
