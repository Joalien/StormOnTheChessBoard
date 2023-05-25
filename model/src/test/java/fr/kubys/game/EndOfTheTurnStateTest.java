package fr.kubys.game;

import fr.kubys.card.QuadrilleCard;
import fr.kubys.game.GameStateController;
import fr.kubys.game.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.kubys.game.exception.AlreadyMovedException;
import fr.kubys.game.exception.CardAlreadyPlayedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static fr.kubys.core.Position.e2;
import static fr.kubys.core.Position.e4;

class EndOfTheTurnStateTest {
    private QuadrilleCard card;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
        gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        card = new QuadrilleCard();
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
        assertThrows(AlreadyMovedException.class, () -> gameStateController.tryToMove(e2, e4));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(CardAlreadyPlayedException.class, () -> gameStateController.tryToPlayCard(card, List.of(QuadrilleCard.Direction.CLOCKWISE)));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }
}