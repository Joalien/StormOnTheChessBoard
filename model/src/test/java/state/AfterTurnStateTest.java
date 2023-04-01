package state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AfterTurnStateTest {
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade.startGame();
        chessBoardFacade.setState(new AfterTurnState());
    }

    @Test
    void should_go_to_end_of_the_turn() {

    }
}