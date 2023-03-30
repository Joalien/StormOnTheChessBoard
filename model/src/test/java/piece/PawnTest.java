package piece;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Test
    void spawn_pawn() {
        Pawn pawn = new WhitePawn();
        pawn.setSquare(new Square("e4"));

        assertEquals(5, pawn.getX());
        assertEquals(4, pawn.getY());
        assertEquals("e4", pawn.getPosition());
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
            pawn.setSquare(new Square("e7"));

            assertEquals(Collections.emptySet(), pawn.squaresOnThePath("a6"));
        }

        @Test
        void should_return_all_square_it_will_go_through() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e2"));

            assertEquals(Set.of("e3"), pawn.squaresOnThePath("e4"));
        }

        @Test
        void should_return_all_square_it_will_go_through_bis() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e3"));

            assertEquals(Collections.emptySet(), pawn.squaresOnThePath("e4"));
        }

        @Test
        void should_not_be_reachable() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e2"));

            assertEquals(Collections.emptySet(), pawn.squaresOnThePath("h8"));
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_white_pawn_on_empty_cell() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e2"));


            assertTrue(pawn.isPositionTheoricallyReachable("e3", Optional.empty()));
            assertTrue(pawn.isPositionTheoricallyReachable("e4", Optional.empty()));
            assertFalse(pawn.isPositionTheoricallyReachable("d3", Optional.empty()));
            assertFalse(pawn.isPositionTheoricallyReachable("f3", Optional.empty()));

            assertFalse(pawn.isPositionTheoricallyReachable("e1", Optional.empty()));
            assertFalse(pawn.isPositionTheoricallyReachable("f1", Optional.empty()));
            assertFalse(pawn.isPositionTheoricallyReachable("d1", Optional.empty()));
        }

        @Test
        void should_not_move_white_pawn_on_white_cell() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e2"));

            assertFalse(pawn.isPositionTheoricallyReachable("e3", Optional.of(Color.WHITE)));
            assertFalse(pawn.isPositionTheoricallyReachable("e4", Optional.of(Color.WHITE)));
            assertFalse(pawn.isPositionTheoricallyReachable("d3", Optional.of(Color.WHITE)));
            assertFalse(pawn.isPositionTheoricallyReachable("f3", Optional.of(Color.WHITE)));

            assertFalse(pawn.isPositionTheoricallyReachable("e1", Optional.of(Color.WHITE)));
            assertFalse(pawn.isPositionTheoricallyReachable("f1", Optional.of(Color.WHITE)));
            assertFalse(pawn.isPositionTheoricallyReachable("d1", Optional.of(Color.WHITE)));
        }

        @Test
        void should_move_white_pawn_on_black_cell() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e2"));

            assertFalse(pawn.isPositionTheoricallyReachable("e3", Optional.of(Color.BLACK)));
            assertFalse(pawn.isPositionTheoricallyReachable("e4", Optional.of(Color.BLACK)));
            assertTrue(pawn.isPositionTheoricallyReachable("d3", Optional.of(Color.BLACK)));
            assertTrue(pawn.isPositionTheoricallyReachable("f3", Optional.of(Color.BLACK)));

            assertFalse(pawn.isPositionTheoricallyReachable("e1", Optional.of(Color.BLACK)));
            assertFalse(pawn.isPositionTheoricallyReachable("f1", Optional.of(Color.BLACK)));
            assertFalse(pawn.isPositionTheoricallyReachable("d1", Optional.of(Color.BLACK)));
        }

        @Test
        void should_move_black_pawn() {
            Pawn pawn = new BlackPawn();
            pawn.setSquare(new Square("e2"));

            assertFalse(pawn.isPositionTheoricallyReachable("e3", Optional.empty()));
            assertFalse(pawn.isPositionTheoricallyReachable("e4", Optional.empty()));
            assertFalse(pawn.isPositionTheoricallyReachable("d3", Optional.empty()));
            assertFalse(pawn.isPositionTheoricallyReachable("f3", Optional.empty()));

            assertTrue(pawn.isPositionTheoricallyReachable("e1", Optional.empty()));
            assertTrue(pawn.isPositionTheoricallyReachable("f1", Optional.of(Color.WHITE)));
            assertTrue(pawn.isPositionTheoricallyReachable("d1", Optional.of(Color.WHITE)));
        }

        @Test
        void should_not_move() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e2"));

            assertFalse(pawn.isPositionTheoricallyReachable("d4", Optional.empty()));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Pawn pawn = new WhitePawn();
            pawn.setSquare(new Square("e5"));

            assertFalse(pawn.isPositionTheoricallyReachable("e5", Optional.of(Color.WHITE)));
        }
    }


}