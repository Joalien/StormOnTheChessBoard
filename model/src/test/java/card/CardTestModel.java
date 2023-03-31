package card;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTestModel {

    private ChessBoard chessBoard;
    private SCCard bombing;
    private String e4 = "e4";

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
//        bombing = new XXX();
    }

    @Nested
    class Success {
        @Test
        void should_work() {

        }
    }

    @Nested
    class Failure {
        @Test
        void should_fail() {
        }
    }
}