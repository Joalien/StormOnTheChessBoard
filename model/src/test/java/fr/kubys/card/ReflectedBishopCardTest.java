package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.ReflectedBishopCard.ReflectedBishopCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Rock;
import fr.kubys.piece.WhitePawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Ref;
import java.util.List;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ReflectedBishopCardTest {

    private Bishop bishop;
    private ReflectedBishopCard reflectedBishop;
    private ChessBoard chessBoard;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        bishop = new Bishop(Color.WHITE);
        chessBoard.add(bishop, e2);
        reflectedBishop = new ReflectedBishopCard();
    }

    @Nested
    class Success {
        @Test
        void should_bounce_once() {
            assertDoesNotThrow(() -> reflectedBishop.playOn(chessBoard, new ReflectedBishopCardParam(bishop, f7)));

            assertEquals(bishop, chessBoard.at(f7).getPiece().get());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void should_bounce_three_times() {
            chessBoard.add(new WhitePawn(), f3);
            assertDoesNotThrow(() -> reflectedBishop.playOn(chessBoard, new ReflectedBishopCardParam(bishop, f7)));

            assertEquals(bishop, chessBoard.at(f7).getPiece().get());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void should_bounce_to_g4_after_5_bounces() {
            chessBoard.add(new WhitePawn(), f1);
            chessBoard.add(new WhitePawn(), f3);
            chessBoard.add(new WhitePawn(), f5);
            reflectedBishop = new ReflectedBishopCard();
            assertDoesNotThrow(() -> reflectedBishop.playOn(chessBoard, new ReflectedBishopCardParam(bishop, g4)));

            assertEquals(bishop, chessBoard.at(g4).getPiece().get());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void should_bounce_to_block_check_even_if_pinned() {
            chessBoard.add(new King(Color.WHITE), h2);
            chessBoard.add(new Rock(Color.BLACK), a2);

            assertDoesNotThrow(() -> new ReflectedBishopCard().playOn(chessBoard, new ReflectedBishopCardParam(bishop, g2)));

            assertEquals(bishop, chessBoard.at(g2).getPiece().get());
        }
    }

    @Nested
    class Failure {

        @Test
        void should_not_take_enemy_king() {
            King king = new King(Color.BLACK);
            chessBoard.add(king, f7);

            assertThrows(IllegalStateException.class, () -> reflectedBishop.playOn(chessBoard, new ReflectedBishopCardParam(bishop, f7)));

            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
            assertEquals(king, chessBoard.at(f7).getPiece().get());
            assertEquals(f7, king.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_move_if_it_check_itself() {
            chessBoard.add(new King(Color.WHITE), h2);
            chessBoard.add(new Rock(Color.BLACK), a2);

            assertThrows(CheckException.class, () -> reflectedBishop.playOn(chessBoard, new ReflectedBishopCardParam(bishop, f7)));

            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
        }

        @Test
        void should_never_bounce_to_f7() {
            chessBoard.add(new WhitePawn(), d1);
            chessBoard.add(new WhitePawn(), f3);

            assertThrows(IllegalArgumentException.class, () -> reflectedBishop.playOn(chessBoard, new ReflectedBishopCardParam(bishop, f7)));

            assertTrue(chessBoard.at(f7).getPiece().isEmpty());
            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
        }

        @Test
        void should_not_be_able_to_bounce_to_itself() {
            reflectedBishop = new ReflectedBishopCard();

            assertThrows(IllegalArgumentException.class, () -> reflectedBishop.playOn(chessBoard, new ReflectedBishopCardParam(bishop, e2)));

            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
        }
    }
}