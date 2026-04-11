package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.piece.Knight;
import fr.kubys.piece.extra.Kangaroo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.b1;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


class KangarooTest {

    private ChessBoard chessBoard;
    private Knight knight;
    private KangarooCard kangaroo;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        knight = (Knight) chessBoard.at(b1).getPiece().get();
        kangaroo = new KangarooCard();
    }

    @Nested
    class Success {
        @Test
        void should_work() {
            assertDoesNotThrow(() -> kangaroo.playOn(chessBoard, new KnightCardParam(knight)));
            assertInstanceOf(Kangaroo.class, chessBoard.at(b1).getPiece().get());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_fail() {
        }
    }
}