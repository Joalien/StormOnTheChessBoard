package piece;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import position.PositionUtil;
import position.Square;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class QueenTest {

    @Test
    void spawn_queen() {
        Queen queen = new Queen(Color.WHITE);
        queen.setSquare(new Square("e4"));


        assertEquals(5, queen.getX());
        assertEquals(4, queen.getY());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            Queen queen = new Queen(Color.WHITE);
            queen.setSquare(new Square("c6"));


            assertEquals(Collections.emptySet(), queen.squaresOnThePath("b5"));
        }

        @Test
        void should_return_all_square_it_will_go_through() {
            Queen queen = new Queen(Color.WHITE);
            queen.setSquare(new Square("a1"));

            assertEquals(Set.of("b2", "c3", "d4", "e5"), queen.squaresOnThePath("f6"));
        }

        @Test
        void should_return_all_square_it_will_go_through_bis() {
            Queen queen = new Queen(Color.WHITE);
            queen.setSquare(new Square("e4"));

            assertEquals(Set.of("d3"), queen.squaresOnThePath("c2"));
        }

        @Test
        void should_return_all_square_it_will_go_through_ter() {
            Queen queen = new Queen(Color.BLACK);
            queen.setSquare(new Square("e4"));

            assertEquals(Set.of("b4", "c4", "d4"), queen.squaresOnThePath("a4"));
        }

        @Test
        void should_not_be_reachable() {
            Queen queen = new Queen(Color.WHITE);
            queen.setSquare(new Square("b7"));

            assertThrows(IllegalArgumentException.class, () -> queen.squaresOnThePath("e8"));
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_on_cells_from_center() {
            Set<String> bishopMovements = Set.of("f3", "g2", "h1", "d5", "c6", "b7", "a8", "d3", "c2", "b1", "f5", "g6", "h7");
            Set<String> rockMovements = Set.of("e3", "e2", "e1", "e5", "e6", "e7", "e8", "d4", "c4", "b4", "a4", "f4", "g4", "h4");
            Set<String> validMoves = Stream.concat(bishopMovements.stream(), rockMovements.stream()).collect(Collectors.toSet());
            Queen queen = new Queen(Color.WHITE);
            queen.setSquare(new Square("e4"));


            assertTrue(PositionUtil.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == queen.reachableSquares(s)));
        }

        @Test
        void should_move_on_cells_from_corner() {
            Set<String> bishopMovements = Set.of("b2", "c3", "d4", "e5", "f6", "g7", "h8");
            Set<String> rockMovements = Set.of("a2", "a3", "a4", "a5", "a6", "a7", "a8", "d1", "c1", "b1", "e1", "f1", "g1", "h1");
            Set<String> validMoves = Stream.concat(bishopMovements.stream(), rockMovements.stream()).collect(Collectors.toSet());
            Queen queen = new Queen(Color.WHITE);
            queen.setSquare(new Square("a1"));

            assertTrue(PositionUtil.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == queen.reachableSquares(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Queen queen = new Queen(Color.WHITE);
            queen.setSquare(new Square("e5"));

            assertFalse(queen.reachableSquares("e5"));
        }
    }


}