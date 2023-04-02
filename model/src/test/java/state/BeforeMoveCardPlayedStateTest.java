package state;

import card.QuadrilleCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import state.exception.CardAlreadyPlayedException;

import static org.junit.jupiter.api.Assertions.*;

class BeforeMoveCardPlayedStateTest {
    private QuadrilleCard card;
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
        chessBoardFacade.setState(StateEnum.BEFORE_MOVE);
        card = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
    }

    @Test
    void should_be_able_to_play_a_valid_move() {
        assertTrue(chessBoardFacade.tryToMove("e2", "e4"));

        assertEquals(StateEnum.END_OF_THE_TURN, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_play_an_invalid_move() {
        assertFalse(chessBoardFacade.tryToMove("e2", "e5"));

        assertEquals(StateEnum.BEFORE_MOVE, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(CardAlreadyPlayedException.class, () -> chessBoardFacade.tryToPlayCard(card));

        assertEquals(StateEnum.BEFORE_MOVE, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_pass_turn() {
        assertFalse(chessBoardFacade.tryToPass());

        assertEquals(StateEnum.BEFORE_MOVE, chessBoardFacade.getState());
    }
}