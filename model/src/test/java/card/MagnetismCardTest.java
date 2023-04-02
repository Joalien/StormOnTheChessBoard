package card;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Piece;
import piece.Queen;

import static org.junit.jupiter.api.Assertions.*;

class MagnetismCardTest {

    private ChessBoard chessBoard;
    private SCCard magnetismCard;
    private String c2 = "c2";
    private Piece piece;


    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        piece = chessBoard.at(c2).getPiece().get();
        magnetismCard = new MagnetismCard(piece);
    }

    @Nested
    class Success {
        @Test
        void should_add_effect_magnetism_on_pawn_c2() {
            assertTrue(magnetismCard.playOn(chessBoard));
            assertEquals(1, chessBoard.getEffects().size());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throws_if_piece_not_on_the_board() {
            assertThrows(IllegalArgumentException.class, () -> new MagnetismCard(new Queen(Color.WHITE)).playOn(chessBoard));
        }

        @Test
        @Disabled("fixme when concept of player will exist")
        void should_throw_if_cast_on_enemy_piece() {
            assertThrows(IllegalArgumentException.class, () -> new MagnetismCard(new Queen(Color.BLACK)).playOn(chessBoard));
        }
    }
}