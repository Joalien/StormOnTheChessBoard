package state;

import card.QuadrilleCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AfterTurnStateTest {
    private QuadrilleCard card;
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
        chessBoardFacade.setState(StateEnum.AFTER_TURN);
        card = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
    }

    @Test
    void should_go_to_end_of_the_turn() {
        assertTrue(chessBoardFacade.tryToPass());

        assertEquals(chessBoardFacade.getState(), StateEnum.END_OF_THE_TURN);
    }

    @Test
    void should_not_be_able_to_play_a_move() {
        assertThrows(AlreadyMovedException.class, () -> chessBoardFacade.tryToMove("e2", "e4"));

        assertEquals(chessBoardFacade.getState(), StateEnum.AFTER_TURN);
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(AlreadyPlayedACardException.class, () -> chessBoardFacade.tryToPlayCard(card));

        assertEquals(chessBoardFacade.getState(), StateEnum.AFTER_TURN);
    }
}