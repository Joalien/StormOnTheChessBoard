package state;

import card.BombingCard;
import card.LightweightSquadCard;
import card.QuadrilleCard;
import card.SCCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Pawn;

import static org.junit.jupiter.api.Assertions.*;

class BeginningOfTheTurnStateTest {

    private SCCard beforeTurnCard;
    private SCCard replaceTurnCard;
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
        chessBoardFacade.setState(StateEnum.BEGINNING_OF_THE_TURN);
        beforeTurnCard = new BombingCard("e4", Color.BLACK);
        replaceTurnCard = new LightweightSquadCard((Pawn) chessBoardFacade.getChessBoard().at("e2").getPiece().get(), (Pawn) chessBoardFacade.getChessBoard().at("d2").getPiece().get());
    }

    @Test
    void should_be_able_to_play_valid_move() {
        assertTrue(chessBoardFacade.tryToMove("e2", "e4"));

        assertEquals(StateEnum.SIMPLE_TURN, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_play_invalid_move() {
        assertFalse(chessBoardFacade.tryToMove("e2", "e5"));

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
    }

    @Nested
    class PlayCard {
        @Test
        void should_be_able_to_play_valid_before_turn_card() {
            assertTrue(chessBoardFacade.tryToPlayCard(beforeTurnCard));

            assertEquals(StateEnum.BEFORE_TURN, chessBoardFacade.getState());
        }

        @Test
        void should_be_able_to_play_valid_replace_turn_card() {
            assertTrue(chessBoardFacade.tryToPlayCard(replaceTurnCard));

            assertEquals(StateEnum.REPLACE_TURN, chessBoardFacade.getState());
        }

        @Test
        void should_not_be_able_to_play_after_turn_card() {
            SCCard afterTurnCard = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
            assertThrows(IllegalStateException.class, () -> chessBoardFacade.tryToPlayCard(afterTurnCard));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
        }

        @Test
        void should_not_be_able_to_play_invalid_card() {
            assertThrows(IllegalArgumentException.class, () -> chessBoardFacade.tryToPlayCard(new BombingCard("h8", Color.BLACK)));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
        }
    }

    @Test
    void should_not_be_able_to_pass() {
        assertFalse(chessBoardFacade.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
    }
}