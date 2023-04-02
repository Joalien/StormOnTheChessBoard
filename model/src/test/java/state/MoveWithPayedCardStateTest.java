package state;

import card.QuadrilleCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import state.exception.AlreadyMovedException;
import state.exception.CardAlreadyPlayedException;

import static org.junit.jupiter.api.Assertions.*;

class MoveWithPayedCardStateTest {
    private QuadrilleCard card;
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
        chessBoardFacade.setState(StateEnum.MOVE_WITH_CARD_PLAYED);
        card = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
    }

    @Test
    void should_go_to_end_of_the_turn() {
        assertTrue(chessBoardFacade.tryToPass());

        assertEquals(StateEnum.END_OF_THE_TURN, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_play_a_move() {
        assertThrows(AlreadyMovedException.class, () -> chessBoardFacade.tryToMove("e2", "e4"));

        assertEquals(StateEnum.MOVE_WITH_CARD_PLAYED, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(CardAlreadyPlayedException.class, () -> chessBoardFacade.tryToPlayCard(card));

        assertEquals(StateEnum.MOVE_WITH_CARD_PLAYED, chessBoardFacade.getState());
    }
}