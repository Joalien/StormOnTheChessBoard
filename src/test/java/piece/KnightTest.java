package piece;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import position.PositionUtil;
import position.Square;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    @Test
    void spawn_knight() {
        Knight knight = new Knight(Color.WHITE);
        knight.setSquare(new Square("e4"));


        assertEquals(5, knight.getX());
        assertEquals(4, knight.getY());
    }

    @Test
    void should_return_all_square_it_will_go_through() {
        Knight knight = new Knight(Color.WHITE);

        assertTrue(knight.squaresOnThePath("c2").isEmpty());
    }

    @Nested
    class Movements {
        @Test
        void move_knight_in_the_middle_of_the_board() {
            List<String> validMoves = List.of("g1", "e1", "d2", "d4", "e5", "g5", "h4", "h2");
            Knight knight = new Knight(Color.WHITE);
            knight.setSquare(new Square("f3"));

            assertTrue(PositionUtil.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == knight.reachableSquares(s)));
        }

        @Test
        void move_knight_in_the_corner() {
            List<String> validMoves = List.of("f2", "g3");
            Knight knight = new Knight(Color.WHITE);
            knight.setSquare(new Square("h1"));

            assertTrue(PositionUtil.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == knight.reachableSquares(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Knight knight = new Knight(Color.WHITE);
            knight.setSquare(new Square("e5"));

            assertFalse(knight.reachableSquares("e5"));
        }
    }

    @Test
    void should_print_itself() {
        Knight knight = new Knight(Color.WHITE);

        assertEquals("white Knight", knight.toString());
    }
}