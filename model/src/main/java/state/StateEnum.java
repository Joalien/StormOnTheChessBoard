package state;

import card.SCCard;
import lombok.Getter;

@Getter
public enum StateEnum implements State{
    BEGINNING_OF_THE_TURN(new BeginningOfTheTurnState()),
    BEFORE_TURN(new BeforeTurnState()),
    SIMPLE_TURN(new SimpleTurn()),
    REPLACE_TURN(new ReplaceTurnState()),
    AFTER_TURN(new AfterTurnState()),
    END_OF_THE_TURN(new EndOfTheTurnState());

    private final State state;
    StateEnum(State state) {
        this.state = state;
    }

    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        return this.state.tryToMove(chessBoardFacade, from, to);
    }

    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        return this.state.tryToPlayCard(chessBoardFacade, card);
    }

    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        return this.state.tryToPass(chessBoardFacade);
    }
}
