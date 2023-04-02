package state;

import card.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Pawn;
import state.exception.AlreadyMovedException;

import static org.junit.jupiter.api.Assertions.*;

class MoveWithoutCardPlayedStateTest {

    private SCCard beforeMoveCard;
    private SCCard replaceMoveCard;
    private SCCard afterMoveCard;
    private ChessBoardFacade chessBoardFacade;

    @BeforeEach
    void setUp() {
        chessBoardFacade = new ChessBoardFacade();
        chessBoardFacade.startGame();
        chessBoardFacade.setState(StateEnum.MOVE_WITHOUT_CARD_PLAYED);
        beforeMoveCard = new BombingCard("e4", Color.BLACK);
        replaceMoveCard = new LightweightSquadCard((Pawn) chessBoardFacade.getChessBoard().at("e2").getPiece().get(), (Pawn) chessBoardFacade.getChessBoard().at("d2").getPiece().get());
        afterMoveCard = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
    }

    @Test
    void should_not_be_able_to_play_move() {
        assertThrows(AlreadyMovedException.class, () -> chessBoardFacade.tryToMove("e2", "e4"));

        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, chessBoardFacade.getState());
    }

    @Test
    void should_be_able_to_pass() {
        assertTrue(chessBoardFacade.tryToPass());

        assertEquals(StateEnum.END_OF_THE_TURN, chessBoardFacade.getState());
    }

    @Nested
    class PlayCard {
        @Test
        void should_be_able_to_play_valid_after_move_card() {
            assertTrue(chessBoardFacade.tryToPlayCard(afterMoveCard));

            assertEquals(StateEnum.END_OF_THE_TURN, chessBoardFacade.getState());
        }

        @Test
        void should_not_be_able_to_play_replace_move_card() {
            assertThrows(IllegalStateException.class, () -> chessBoardFacade.tryToPlayCard(replaceMoveCard));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, chessBoardFacade.getState());
        }

        @Test
        void should_not_be_able_to_play_before_move_card() {
            assertThrows(IllegalStateException.class, () -> chessBoardFacade.tryToPlayCard(beforeMoveCard));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, chessBoardFacade.getState());
        }

        @Test
        void should_not_be_able_to_play_invalid_card() {
            assertThrows(IllegalArgumentException.class, () -> chessBoardFacade.tryToPlayCard(new BlackHoleCard("h8")));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, chessBoardFacade.getState());
        }
    }
}