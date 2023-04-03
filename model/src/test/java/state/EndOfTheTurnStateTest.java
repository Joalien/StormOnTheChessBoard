package state;

import card.QuadrilleCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import state.exception.AlreadyMovedException;
import state.exception.CardAlreadyPlayedException;

import static org.junit.jupiter.api.Assertions.*;

class EndOfTheTurnStateTest {
    private QuadrilleCard card;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
        gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        card = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
    }

    @Test
    void should_go_to_end_of_the_turn() {
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
        assertTrue(gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        assertEquals(gameStateController.getBlack(), gameStateController.getCurrentPlayer());
    }

    @Test
    void should_change_color() {
        gameStateController.setCurrentPlayer(gameStateController.getBlack());

        assertTrue(gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
    }

    @Test
    void should_not_be_able_to_play_a_move() {
        assertThrows(AlreadyMovedException.class, () -> gameStateController.tryToMove("e2", "e4"));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(CardAlreadyPlayedException.class, () -> gameStateController.tryToPlayCard(card));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }
}