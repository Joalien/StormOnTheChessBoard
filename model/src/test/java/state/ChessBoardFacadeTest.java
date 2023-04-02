package state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import piece.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ChessBoardFacadeTest {

    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
    }

    @Test
    void should_set_up_correctly() {
        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
        assertEquals(chessBoardFacade.getWhite(), chessBoardFacade.getCurrentPlayer());
        assertEquals(5, chessBoardFacade.getWhite().getCards().size());
        assertEquals(5, chessBoardFacade.getBlack().getCards().size());
        assertEquals(Color.WHITE, chessBoardFacade.getWhite().getColor());
        assertEquals(Color.BLACK, chessBoardFacade.getBlack().getColor());
        assertFalse(chessBoardFacade.getCards().isEmpty());
    }
}