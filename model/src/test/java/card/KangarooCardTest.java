package card;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.extra.Kangaroo;
import piece.Knight;
import piece.Piece;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class KangarooCardTest {

    private ChessBoard chessBoard;
    private Knight knight;
    private SCCard kangaroo;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        knight = (Knight) chessBoard.at("b1").getPiece().get();
        kangaroo = new KangarooCard(knight);
    }

    @Nested
    class Success {
        @Test
        void should_work() {
            assertTrue(kangaroo.playOn(chessBoard));
        }

        @Test
        void should_return_reachable_position() {
            Set<String> validMoves = Set.of("b5", "c4", "a4", "d5", "e4", "b3", "f3");
            assertTrue(kangaroo.playOn(chessBoard));

            Piece kangaroo = chessBoard.at("b1").getPiece().get();
            assertTrue(kangaroo instanceof Kangaroo);
            assertEquals(validMoves, chessBoard.getAllAttackablePosition(kangaroo));
        }

    }

    @Nested
    class Failure {
        @Test
        void should_fail() {
        }
    }
}