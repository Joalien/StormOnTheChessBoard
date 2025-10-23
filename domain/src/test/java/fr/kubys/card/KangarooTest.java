package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KangarooCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;
import fr.kubys.piece.extra.Kangaroo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;


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
            assertDoesNotThrow(() -> kangaroo.playOn(chessBoard, new KangarooCardParam(knight)));
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