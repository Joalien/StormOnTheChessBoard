package card;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Piece;
import piece.Queen;
import position.Position;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MagnetismCardTest {

    private ChessBoard chessBoard;
    private Card magnetismCard;
    private final Position c2 = Position.c2;
    private Piece piece;


    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        piece = chessBoard.at(c2).getPiece().get();
        magnetismCard = new MagnetismCard();
    }

    @Nested
    class Success {
        @Test
        void should_add_effect_magnetism_on_pawn_c2() {
            assertTrue(magnetismCard.playOn(chessBoard, List.of(piece)));
            assertEquals(1, chessBoard.getEffects().size());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throws_if_piece_not_on_the_board() {
            assertThrows(IllegalArgumentException.class, () -> new MagnetismCard().playOn(chessBoard, List.of(new Queen(Color.WHITE))));
        }

        @Test
        @Disabled("fixme when concept of player will exist")
        void should_throw_if_cast_on_enemy_piece() {
            assertThrows(IllegalArgumentException.class, () -> new MagnetismCard().playOn(chessBoard, List.of(new Queen(Color.BLACK))));
        }
    }
}