package card;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.King;
import piece.Queen;

import static org.junit.jupiter.api.Assertions.*;

class BombingCardTest {

    private ChessBoard chessBoard;
    private SCCard bombing;
    private final String e4 = "e4";

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        bombing = new BombingCard(e4, Color.BLACK);
    }

    @Nested
    class Success {
        @Test
        void should_work_if_empty_square() {
            assertTrue(bombing.playOn(chessBoard));
        }

        @Test
        void should_work_if_played_on_ally_piece() {
            chessBoard.add(new Queen(Color.WHITE), e4);

            assertTrue(bombing.playOn(chessBoard));
        }

        @Test
        void should_work_if_played_ally_piece_moves_on_it() {
            assertTrue(bombing.playOn(chessBoard));
            Queen queen = new Queen(Color.WHITE);

            chessBoard.add(queen, e4);

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
        }

        @Test
        void should_not_explode_if_enemy_king() {
            assertTrue(bombing.playOn(chessBoard));
            King king = new King(Color.BLACK);

            chessBoard.add(king, e4);

            assertEquals(king, chessBoard.at(e4).getPiece().get());
        }

        @Test
        void should_not_explode_if_enemy_piece_move_nearby() {
            assertTrue(bombing.playOn(chessBoard));
            Queen queen = new Queen(Color.BLACK);

            chessBoard.add(queen, "d4");

            assertEquals(queen, chessBoard.at("d4").getPiece().get());
        }

        @Test
        void should_explode_enemy_piece() {
            assertTrue(bombing.playOn(chessBoard));
            Queen queen = new Queen(Color.BLACK);

            chessBoard.add(queen, e4);

            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
        }

        @Test
        void should_explode_enemy_piece_only_once() {
            assertTrue(bombing.playOn(chessBoard));
            Queen queen = new Queen(Color.BLACK);

            chessBoard.add(queen, e4);

            assertTrue(chessBoard.at(e4).getPiece().isEmpty());

            chessBoard.add(queen, e4);

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
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