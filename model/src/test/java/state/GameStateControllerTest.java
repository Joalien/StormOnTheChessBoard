package state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import piece.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GameStateControllerTest {

    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
    }

    @Test
    void should_set_up_correctly() {
        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getState());
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
        assertEquals(5, gameStateController.getWhite().getCards().size());
        assertEquals(5, gameStateController.getBlack().getCards().size());
        assertEquals(Color.WHITE, gameStateController.getWhite().getColor());
        assertEquals(Color.BLACK, gameStateController.getBlack().getColor());
        assertFalse(gameStateController.getCards().isEmpty());
    }
}