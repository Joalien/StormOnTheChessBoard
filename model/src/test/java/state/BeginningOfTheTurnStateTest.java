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

    private SCCard beforeMoveCard;
    private SCCard replaceMoveCard;
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
        chessBoardFacade.setState(StateEnum.BEGINNING_OF_THE_TURN);
        beforeMoveCard = new BombingCard("e4", Color.BLACK);
        replaceMoveCard = new LightweightSquadCard((Pawn) chessBoardFacade.getChessBoard().at("e2").getPiece().get(), (Pawn) chessBoardFacade.getChessBoard().at("d2").getPiece().get());
    }

    @Test
    void should_be_able_to_play_valid_move() {
        assertTrue(chessBoardFacade.tryToMove("e2", "e4"));

        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_play_invalid_move() {
        assertFalse(chessBoardFacade.tryToMove("e2", "e5"));

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
    }

    @Test
    void should_not_be_able_to_pass() {
        assertFalse(chessBoardFacade.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
    }

    @Nested
    class PlayCard {
        @Test
        void should_be_able_to_play_valid_before_move_card() {
            assertTrue(chessBoardFacade.tryToPlayCard(beforeMoveCard));

            assertEquals(StateEnum.BEFORE_MOVE, chessBoardFacade.getState());
        }

        @Test
        void should_be_able_to_play_valid_replace_move_card() {
            assertTrue(chessBoardFacade.tryToPlayCard(replaceMoveCard));

            assertEquals(StateEnum.END_OF_THE_TURN, chessBoardFacade.getState());
        }

        @Test
        void should_not_be_able_to_play_after_move_card() {
            SCCard afterMoveCard = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
            assertThrows(IllegalStateException.class, () -> chessBoardFacade.tryToPlayCard(afterMoveCard));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
            // assert card effect has not been done
            assertEquals(Color.BLACK, chessBoardFacade.getChessBoard().at("a8").getPiece().get().getColor());
            assertEquals(Color.BLACK, chessBoardFacade.getChessBoard().at("h8").getPiece().get().getColor());
            assertEquals(Color.WHITE, chessBoardFacade.getChessBoard().at("a1").getPiece().get().getColor());
            assertEquals(Color.WHITE, chessBoardFacade.getChessBoard().at("h1").getPiece().get().getColor());
        }

        @Test
        void should_not_be_able_to_play_invalid_card() {
            assertThrows(IllegalArgumentException.class, () -> chessBoardFacade.tryToPlayCard(new BombingCard("h8", Color.BLACK)));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, chessBoardFacade.getState());
        }
    }
}