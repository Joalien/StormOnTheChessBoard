package card;

import board.CheckException;
import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.*;

import static org.junit.jupiter.api.Assertions.*;

class ReflectedBishopCardTest {

    public static final String e2 = "e2";
    public static final String f7 = "f7";
    private Bishop bishop;
    private SCCard reflectedBishop;
    private ChessBoard chessBoard;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        bishop = new Bishop(Color.WHITE);
        chessBoard.add(bishop, e2);
        reflectedBishop = new ReflectedBishopCard(bishop, f7);
    }

    @Nested
    class Success {
        @Test
        void should_bounce_once() {
            assertTrue(reflectedBishop.play(chessBoard));

            assertEquals(bishop, chessBoard.at(f7).getPiece().get());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void should_bounce_three_times() {
            chessBoard.add(new WhitePawn(), "f3");
            assertTrue(reflectedBishop.play(chessBoard));

            assertEquals(bishop, chessBoard.at(f7).getPiece().get());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void should_bounce_to_g4_after_5_bounces() {
            chessBoard.add(new WhitePawn(), "f1");
            chessBoard.add(new WhitePawn(), "f3");
            chessBoard.add(new WhitePawn(), "f5");
            String g4 = "g4";
            reflectedBishop = new ReflectedBishopCard(bishop, g4);
            assertTrue(reflectedBishop.play(chessBoard));

            assertEquals(bishop, chessBoard.at(g4).getPiece().get());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void should_bounce_to_block_check_even_if_pinned() {
            chessBoard.add(new King(Color.WHITE), "h2");
            chessBoard.add(new Rock(Color.BLACK), "a2");
            String g2 = "g2";

            assertTrue(new ReflectedBishopCard(bishop, g2).play(chessBoard));

            assertEquals(bishop, chessBoard.at(g2).getPiece().get());
        }
    }

    @Nested
    class Failure {

        @Test
        void should_not_take_enemy_king() {
            King king = new King(Color.BLACK);
            chessBoard.add(king, f7);

            assertThrows(IllegalStateException.class, () -> reflectedBishop.play(chessBoard));

            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
            assertEquals(king, chessBoard.at(f7).getPiece().get());
            assertEquals(f7, king.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_move_if_it_check_itself() {
            chessBoard.add(new King(Color.WHITE), "h2");
            chessBoard.add(new Rock(Color.BLACK), "a2");

            assertThrows(CheckException.class, () -> reflectedBishop.play(chessBoard));

            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
        }

        @Test
        void should_never_bounce_to_f7() {
            chessBoard.add(new WhitePawn(), "d1");
            chessBoard.add(new WhitePawn(), "f3");

            assertThrows(IllegalArgumentException.class, () -> reflectedBishop.play(chessBoard));

            assertTrue(chessBoard.at(f7).getPiece().isEmpty());
            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
        }

        @Test
        void should_not_be_able_to_bounce_to_itself() {
            reflectedBishop = new ReflectedBishopCard(bishop, e2);

            assertThrows(IllegalArgumentException.class, () -> reflectedBishop.play(chessBoard));

            assertEquals(bishop, chessBoard.at(e2).getPiece().get());
        }
    }
}