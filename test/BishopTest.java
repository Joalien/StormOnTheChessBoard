import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    @Test
    void spawn_bishop() {
        Bishop bishop = new Bishop("e4", Color.WHITE);

        assertEquals(5, bishop.x);
        assertEquals(4, bishop.y);
        assertEquals("e4", bishop.getPosition());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            Bishop bishop = new Bishop("c6", Color.WHITE);

            assertEquals(Collections.emptySet(), bishop.squaresOnThePath("b5"));
        }

        @Test
        void should_return_all_square_it_will_go_through() {
            Bishop bishop = new Bishop("a1", Color.WHITE);

            assertEquals(Set.of("b2", "c3", "d4", "e5"), bishop.squaresOnThePath("f6"));
        }

        @Test
        void should_return_all_square_it_will_go_through_bis() {
            Bishop bishop = new Bishop("e4", Color.WHITE);

            assertEquals(Set.of("d3"), bishop.squaresOnThePath("c2"));
        }

        @Test
        void should_return_all_square_it_will_go_through_ter() {
            Bishop bishop = new Bishop("h1", Color.WHITE);

            assertEquals(Set.of("b7", "c6", "d5", "e4", "f3", "g2"), bishop.squaresOnThePath("a8"));
        }

        @Test
        void should_not_be_reachable() {
            Bishop bishop = new Bishop("a1", Color.WHITE);

            assertThrows(IllegalArgumentException.class, () -> bishop.squaresOnThePath("a2"));
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_on_cells_from_center() {
            Set<String> validMoves = Set.of("f3", "g2", "h1", "d5", "c6", "b7", "a8", "d3", "c2", "b1", "f5", "g6", "h7");
            Bishop bishop = new Bishop("e4", Color.WHITE);

            assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == bishop.reachableSquares(s)));
        }

        @Test
        void should_move_on_cells_from_corner() {
            Set<String> validMoves = Set.of("b2", "c3", "d4", "e5", "f6", "g7", "h8");
            Bishop bishop = new Bishop("a1", Color.WHITE);

            assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == bishop.reachableSquares(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Bishop bishop = new Bishop("e5", Color.WHITE);

            assertFalse(bishop.reachableSquares("e5"));
        }
    }


}