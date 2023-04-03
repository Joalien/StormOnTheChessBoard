package state;

import card.BombingCard;
import card.LightweightSquadCard;
import card.QuadrilleCard;
import card.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Pawn;

import static org.junit.jupiter.api.Assertions.*;

class BeginningOfTheTurnStateTest {

    private Card beforeMoveCard;
    private Card replaceMoveCard;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
        gameStateController.setCurrentState(StateEnum.BEGINNING_OF_THE_TURN);
        beforeMoveCard = new BombingCard("e4");
        replaceMoveCard = new LightweightSquadCard((Pawn) gameStateController.getChessBoard().at("e2").getPiece().get(), (Pawn) gameStateController.getChessBoard().at("d2").getPiece().get());
    }

    @Test
    void should_be_able_to_play_valid_move() {
        assertTrue(gameStateController.tryToMove("e2", "e4"));

        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_invalid_move() {
        assertFalse(gameStateController.tryToMove("e2", "e5"));

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_pass() {
        assertFalse(gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Nested
    class PlayCard {
        @Test
        void should_be_able_to_play_valid_before_move_card() {
            assertTrue(gameStateController.tryToPlayCard(beforeMoveCard));

            assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
        }

        @Test
        void should_be_able_to_play_valid_replace_move_card() {
            assertTrue(gameStateController.tryToPlayCard(replaceMoveCard));

            assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_after_move_card() {
            Card afterMoveCard = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
            assertThrows(IllegalStateException.class, () -> gameStateController.tryToPlayCard(afterMoveCard));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
            // assert card effect has not been done
            assertEquals(Color.BLACK, gameStateController.getChessBoard().at("a8").getPiece().get().getColor());
            assertEquals(Color.BLACK, gameStateController.getChessBoard().at("h8").getPiece().get().getColor());
            assertEquals(Color.WHITE, gameStateController.getChessBoard().at("a1").getPiece().get().getColor());
            assertEquals(Color.WHITE, gameStateController.getChessBoard().at("h1").getPiece().get().getColor());
        }

        @Test
        void should_not_be_able_to_play_invalid_card() {
            assertThrows(IllegalArgumentException.class, () -> gameStateController.tryToPlayCard(new BombingCard("h8")));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        }
    }
}