package fr.kubys.game;

import fr.kubys.card.QuadrilleCard;
import fr.kubys.card.params.QuadrilleCardParam;
import fr.kubys.core.Color;
import fr.kubys.game.exception.AlreadyMovedException;
import fr.kubys.game.exception.CardAlreadyPlayedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static fr.kubys.core.Position.e2;
import static fr.kubys.core.Position.e4;
import static org.junit.jupiter.api.Assertions.*;

class EndOfTheTurnStateTest {
    private QuadrilleCard card;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame(new Random().nextLong());
        gameStateController.setCurrentState(StateEnum.END_OF_THE_TURN);
        card = new QuadrilleCard();
        gameStateController.getCurrentPlayer().getCards().add(card);
    }

    @Test
    void should_go_to_end_of_the_turn() {
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
        assertDoesNotThrow(() -> gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        assertEquals(gameStateController.getBlack(), gameStateController.getCurrentPlayer());
    }

    @Test
    void should_change_color() {
        assertEquals(Color.WHITE, gameStateController.getCurrentPlayer().getColor());

        assertDoesNotThrow(() -> gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        assertEquals(Color.BLACK, gameStateController.getCurrentPlayer().getColor());
    }

    @Test
    void should_not_be_able_to_play_a_move() {
        assertThrows(AlreadyMovedException.class, () -> gameStateController.tryToMove(e2, e4));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_a_card() {
        assertThrows(CardAlreadyPlayedException.class, () -> gameStateController.tryToPlayCard(card, new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE)));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }
}