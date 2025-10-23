package fr.kubys.piece.extra;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.KangarooCard;
import fr.kubys.card.params.KangarooCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class KangarooTest {
    private ChessBoard chessBoard;
    private Kangaroo kangaroo;

    @Test
    void spawn_kangaroo() {
        Kangaroo kangaroo = new Kangaroo(Color.WHITE);
        kangaroo.setPosition(b1);

        assertEquals(File.B, kangaroo.getFile());
        assertEquals(Row.One, kangaroo.getRow());
        assertEquals(b1, kangaroo.getPosition());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_path_though_b6_to_move_from_a8_to_c4() {
            Kangaroo kangaroo = new Kangaroo(Color.WHITE);
            kangaroo.setPosition(a8);

            assertEquals(Set.of(b6), kangaroo.squaresOnThePath(c4));
        }
        @Test
        void should_path_though_a6_or_c6_to_move_from_b8_to_b4() {
            Kangaroo kangaroo = new Kangaroo(Color.WHITE);
            kangaroo.setPosition(b8);

            assertEquals(Set.of(a6, c6), kangaroo.squaresOnThePath(b4));
        }
        @Test
        void should_path_though_c3_or_f6_to_move_from_e4_to_d5() {
            Kangaroo kangaroo = new Kangaroo(Color.BLACK);
            kangaroo.setPosition(e4);

            assertEquals(Set.of(c3, f6), kangaroo.squaresOnThePath(d5));
        }

    }

    @Nested
    class Movements {
        @Test
        void should_return_all_reachable_position() {
            chessBoard = ChessBoard.createEmpty();
            kangaroo = new Kangaroo(Color.WHITE);
            chessBoard.add(kangaroo, b1);

            assertEquals(Set.of(b5, c4, c2, a4, d5, e4, a2, e2, b3, d1, f3, f1), chessBoard.getAllAttackablePosition(kangaroo));
        }

        @Test
        void should_move_only_if_first_square_is_empty() {
            chessBoard = ChessBoard.createWithInitialState();
            chessBoard.removePieceFromTheBoard(chessBoard.at(b1).getPiece().get());
            kangaroo = new Kangaroo(Color.WHITE);
            chessBoard.add(kangaroo, b1);

            assertEquals(Set.of(b5, c4, a4, d5, e4), chessBoard.getAllAttackablePosition(kangaroo));
        }
        @Test
        void should_not_move_because_all_path_are_blocked() {
            chessBoard = ChessBoard.createEmpty();
            Kangaroo kangaroo = new Kangaroo(Color.WHITE);
            chessBoard.add(kangaroo, a8);
            chessBoard.add(new Queen(Color.WHITE), c7);
            chessBoard.add(new Queen(Color.BLACK), b6);
            // the kangaroo on a8 cannot move because of both queens blocking the path

            assertTrue(chessBoard.getAllAttackablePosition(kangaroo).isEmpty());
        }
    }


}