package state;

import card.QuadrilleCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplaceStateTest {
    private QuadrilleCard card;
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
        chessBoardFacade.setState(StateEnum.REPLACE_TURN);
        card = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
    }

    @Test
    void should_go_to_end_of_the_turn() {
        assertTrue(chessBoardFacade.tryToPass());

        assertEquals(chessBoardFacade.getState(), StateEnum.END_OF_THE_TURN);
    }

    @Test
    void should_not_be_able_to_play_a_move() {
        assertThrows(IllegalStateException.class, () -> chessBoardFacade.tryToMove("e2", "e4"));

        assertEquals(chessBoardFacade.getState(), StateEnum.REPLACE_TURN);
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(IllegalStateException.class, () -> chessBoardFacade.tryToPlayCard(card));

        assertEquals(chessBoardFacade.getState(), StateEnum.REPLACE_TURN);
    }
}