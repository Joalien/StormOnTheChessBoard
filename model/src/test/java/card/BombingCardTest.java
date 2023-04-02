package card;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Queen;

import static org.junit.jupiter.api.Assertions.*;

class BombingCardTest {

    private final String e4 = "e4";
    private ChessBoard chessBoard;
    private SCCard bombing;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        bombing = new BombingCard(e4, Color.BLACK);
    }

    @Nested
    class Success {
        @Test
        void should_work_if_empty_square() {
            assertTrue(chessBoard.getEffects().isEmpty());

            assertTrue(bombing.playOn(chessBoard));

            assertEquals(1, chessBoard.getEffects().size());
        }

        @Test
        void should_work_if_played_on_ally_piece() {
            chessBoard.add(new Queen(Color.WHITE), e4);

            assertTrue(bombing.playOn(chessBoard));
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throw_if_position_occupied_by_enemy_piece() {
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, e4);

            assertThrows(IllegalArgumentException.class, () -> bombing.playOn(chessBoard));

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
        }
    }
}