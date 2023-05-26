package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Row;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Test
    void spawn_pawn() {
        Pawn pawn = new WhitePawn();
        pawn.setSquare(new Square(e4));

        assertEquals(File.E, pawn.getFile());
        assertEquals(Row.Four, pawn.getRow());
        assertEquals(e4, pawn.getPosition());
    }

    @Test
    void should_print_white_pawn() {
        Pawn pawn = new WhitePawn();

        assertEquals("white Pawn", pawn.toString());
    }

    @Test
    void should_print_black_pawn() {
        Pawn pawn = new BlackPawn();

        assertEquals("black Pawn", pawn.toString());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            Pawn pawn = new BlackPawn();
            pawn.setSquare(new Square(e7));

            assertEquals(Collections.emptySet(), pawn.squaresOnThePath(a6));
        }

        @Test
        void should_return_all_square_it_will_go_through() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e2));

            assertEquals(Set.of(e3), pawn.squaresOnThePath(e4));
        }

        @Test
        void should_return_all_square_it_will_go_through_bis() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e3));

            assertEquals(Collections.emptySet(), pawn.squaresOnThePath(e4));
        }

        @Test
        void should_not_be_reachable() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e2));

            assertEquals(Collections.emptySet(), pawn.squaresOnThePath(h8));
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_white_pawn_on_empty_cell() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e2));

            assertTrue(pawn.isPositionTheoreticallyReachable(e3));
            assertTrue(pawn.isPositionTheoreticallyReachable(e4));
            assertFalse(pawn.isPositionTheoreticallyReachable(d3));
            assertFalse(pawn.isPositionTheoreticallyReachable(f3));

            assertFalse(pawn.isPositionTheoreticallyReachable(e1));
            assertFalse(pawn.isPositionTheoreticallyReachable(f1));
            assertFalse(pawn.isPositionTheoreticallyReachable(d1));
        }

        @Test
        void should_not_move_white_pawn_on_white_cell() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e2));

            assertFalse(pawn.isPositionTheoreticallyReachable(e3, Color.WHITE));
            assertFalse(pawn.isPositionTheoreticallyReachable(e4, Color.WHITE));
            assertFalse(pawn.isPositionTheoreticallyReachable(d3, Color.WHITE));
            assertFalse(pawn.isPositionTheoreticallyReachable(f3, Color.WHITE));

            assertFalse(pawn.isPositionTheoreticallyReachable(e1, Color.WHITE));
            assertFalse(pawn.isPositionTheoreticallyReachable(f1, Color.WHITE));
            assertFalse(pawn.isPositionTheoreticallyReachable(d1, Color.WHITE));
        }

        @Test
        void should_move_white_pawn_on_black_cell() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e2));

            assertFalse(pawn.isPositionTheoreticallyReachable(e3, Color.BLACK));
            assertFalse(pawn.isPositionTheoreticallyReachable(e4, Color.BLACK));
            assertTrue(pawn.isPositionTheoreticallyReachable(d3, Color.BLACK));
            assertTrue(pawn.isPositionTheoreticallyReachable(f3, Color.BLACK));

            assertFalse(pawn.isPositionTheoreticallyReachable(e1, Color.BLACK));
            assertFalse(pawn.isPositionTheoreticallyReachable(f1, Color.BLACK));
            assertFalse(pawn.isPositionTheoreticallyReachable(d1, Color.BLACK));
        }

        @Test
        void should_move_black_pawn() {
            Pawn pawn = new BlackPawn();
            pawn.setSquare(new Square(e2));

            assertFalse(pawn.isPositionTheoreticallyReachable(e3));
            assertFalse(pawn.isPositionTheoreticallyReachable(e4));
            assertFalse(pawn.isPositionTheoreticallyReachable(d3));
            assertFalse(pawn.isPositionTheoreticallyReachable(f3));

            assertTrue(pawn.isPositionTheoreticallyReachable(e1));
            assertTrue(pawn.isPositionTheoreticallyReachable(f1, Color.WHITE));
            assertTrue(pawn.isPositionTheoreticallyReachable(d1, Color.WHITE));
        }

        @Test
        void should_not_move() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e2));

            assertFalse(pawn.isPositionTheoreticallyReachable(d4));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square(e5));

            assertFalse(pawn.isPositionTheoreticallyReachable(e5, Color.WHITE));
        }
    }


}