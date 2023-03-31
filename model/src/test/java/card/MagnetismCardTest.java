package card;

import board.ChessBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Piece;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        void should_work() {
            assertTrue(magnetismCard.playOn(chessBoard));
        }
    }

    @Nested
    class Failure {
        @Test
        void should_fail() {
        }
    }
}