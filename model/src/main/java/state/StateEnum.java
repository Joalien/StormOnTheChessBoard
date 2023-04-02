package state;

import card.SCCard;
import lombok.Getter;

@Getter
public enum StateEnum implements TurnState {
    BEGINNING_OF_THE_TURN(new BeginningOfTheTurnState()),
    BEFORE_MOVE(new BeforeMoveCardPlayedState()),
    MOVE_WITH_CARD_PLAYED(new MoveWithCardPlayedState()),
    MOVE_WITHOUT_CARD_PLAYED(new MoveWithoutCardPlayedState()),
    REPLACE_MOVE(new ReplaceMoveCardPlayedState()),
    AFTER_MOVE(new AfterMoveCardPlayedState()),
    END_OF_THE_TURN(new EndOfTheTurnState());

    private final TurnState turnState;
    StateEnum(TurnState turnState) {
        this.turnState = turnState;
    }

    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        return this.turnState.tryToMove(chessBoardFacade, from, to);
    }

    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        return this.turnState.tryToPlayCard(chessBoardFacade, card);
    }

    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        return this.turnState.tryToPass(chessBoardFacade);
    }
}
