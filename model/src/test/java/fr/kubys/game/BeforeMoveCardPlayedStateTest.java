package fr.kubys.game;

import fr.kubys.card.QuadrilleCard;
import fr.kubys.game.GameStateController;
import fr.kubys.game.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.kubys.game.exception.CardAlreadyPlayedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static fr.kubys.core.Position.*;

class BeforeMoveCardPlayedStateTest {
    private QuadrilleCard card;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
        gameStateController.setCurrentState(StateEnum.BEFORE_MOVE);
        card = new QuadrilleCard();
    }

    @Test
    void should_be_able_to_play_a_valid_move() {
        assertTrue(gameStateController.tryToMove(e2, e4));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_an_invalid_move() {
        assertFalse(gameStateController.tryToMove(e2, e5));

        assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(CardAlreadyPlayedException.class, () -> gameStateController.tryToPlayCard(card, List.of(QuadrilleCard.Direction.CLOCKWISE)));

        assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_pass_turn() {
        assertFalse(gameStateController.tryToPass());

        assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
    }
}