package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KangarooCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.extra.Kangaroo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;


class KangarooCardTest {

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
            assertDoesNotThrow(() -> kangaroo.playOn(chessBoard, new KangarooCardParam(knight)));
        }

        @Test
        void should_return_reachable_position() {
            Set<Position> validMoves = Set.of(b5, c4, a4, d5, e4, b3, f3);
            assertDoesNotThrow(() -> kangaroo.playOn(chessBoard, new KangarooCardParam(knight)));

            Piece kangaroo = chessBoard.at(b1).getPiece().get();
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