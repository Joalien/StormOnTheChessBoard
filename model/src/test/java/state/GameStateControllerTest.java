package state;

import card.Card;
import card.LightweightSquadCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Pawn;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateControllerTest {

    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
    }

    @Test
    void should_set_up_correctly() {
        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
        assertEquals(4, gameStateController.getWhite().getCards().size());
        assertEquals(4, gameStateController.getBlack().getCards().size());
        assertFalse(gameStateController.getCards().isEmpty());
        assertEquals(Color.WHITE, gameStateController.getWhite().getColor());
        assertEquals(Color.BLACK, gameStateController.getBlack().getColor());
    }

    @Test
    void should_discard_card_and_pick_new_one() {
        Card card = new LightweightSquadCard();
        gameStateController.getCurrentPlayer().getCards().clear();
        gameStateController.getCurrentPlayer().getCards().add(card);
        gameStateController.getCards().remove(card);
        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
        assertTrue(gameStateController.getCurrentPlayer().getCards().contains(card));

        assertTrue(gameStateController.tryToPlayCard(card, List.of((Pawn) gameStateController.getChessBoard().at("e2").getPiece().get(), (Pawn) gameStateController.getChessBoard().at("d2").getPiece().get())));

        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
        assertFalse(gameStateController.getCurrentPlayer().getCards().contains(card));
    }

    @Test
    void should_not_be_able_to_start_an_already_started_game() {
        gameStateController = new GameStateController();
        assertTrue(gameStateController.startGame());
        assertFalse(gameStateController.startGame());
    }
}