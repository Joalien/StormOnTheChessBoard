package state;

import card.Card;
import card.LightweightSquadCard;
import card.QuadrilleCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Pawn;

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
        assertEquals(5, gameStateController.getWhite().getCards().size());
        assertEquals(5, gameStateController.getBlack().getCards().size());
        assertFalse(gameStateController.getCards().isEmpty());
        assertEquals(Color.WHITE, gameStateController.getWhite().getColor());
        assertEquals(Color.BLACK, gameStateController.getBlack().getColor());
    }

    @Test
    void should_discard_card_and_pick_new_one() {
        Card card = new LightweightSquadCard((Pawn) gameStateController.getChessBoard().at("e2").getPiece().get(), (Pawn) gameStateController.getChessBoard().at("d2").getPiece().get());
        gameStateController.getCurrentPlayer().getCards().clear();
        gameStateController.getCurrentPlayer().getCards().add(LightweightSquadCard.class);
        gameStateController.getCards().remove(LightweightSquadCard.class);
        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
        assertTrue(gameStateController.getCurrentPlayer().getCards().contains(LightweightSquadCard.class));

        assertTrue(gameStateController.tryToPlayCard(card));

        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
        assertFalse(gameStateController.getCurrentPlayer().getCards().contains(LightweightSquadCard.class));
    }
}