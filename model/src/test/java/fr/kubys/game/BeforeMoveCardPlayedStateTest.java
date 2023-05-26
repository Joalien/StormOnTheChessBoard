package fr.kubys.game;

import fr.kubys.board.IllegalMoveException;
import fr.kubys.card.QuadrilleCard;
import fr.kubys.game.exception.CardAlreadyPlayedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BeforeMoveCardPlayedStateTest {
    private QuadrilleCard card;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
        gameStateController.setCurrentState(StateEnum.BEFORE_MOVE);
        card = new QuadrilleCard();
        gameStateController.getCurrentPlayer().getCards().add(card);
    }

    @Test
    void should_be_able_to_play_a_valid_move() {
        assertDoesNotThrow(() -> gameStateController.tryToMove(e2, e4));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_an_invalid_move() {
        assertThrows(IllegalMoveException.class, () -> gameStateController.tryToMove(e2, e5));

        assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(CardAlreadyPlayedException.class, () -> gameStateController.tryToPlayCard(card, List.of(QuadrilleCard.Direction.CLOCKWISE)));

        assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_pass_turn() {
        assertThrows(IllegalStateException.class, () -> gameStateController.tryToPass());

        assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
    }
}