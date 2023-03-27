import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

class KnightTest {

    @Test
    void spawn_knight() {
        Knight knight = new Knight(5, 4, true, 'K');

        Assertions.assertEquals(5, knight.x);
        Assertions.assertEquals(4, knight.y);
    }

    @Test
    void should_return_all_square_it_will_go_through() {
        Knight knight = new Knight(1, 1, true, 'B');

        Assertions.assertTrue(knight.squaresOnThePath("c2").isEmpty());
    }

    @Nested
    class Movements {
        @Test
        void move_knight_in_the_middle_of_the_board() {
            List<String> validMoves = List.of("g1", "e1", "d2", "d4", "e5", "g5", "h4", "h2");
            Knight knight = new Knight(6, 3, true, 'K');

            Assertions.assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .peek(s -> System.out.println(s + " " + validMoves.contains(s) + " " + knight.reachableSquares(s)))
                    .allMatch(s -> validMoves.contains(s) == knight.reachableSquares(s)));
        }

        @Test
        void move_knight_in_the_corner() {
            List<String> validMoves = List.of("f2", "g3");
            Knight knight = new Knight(8, 1, true, 'K');

            Assertions.assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .peek(s -> System.out.println(s + " " + validMoves.contains(s) + " " + knight.reachableSquares(s)))
                    .allMatch(s -> validMoves.contains(s) == knight.reachableSquares(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Knight knight = new Knight(5, 5, true, 'B');

            Assertions.assertFalse(knight.reachableSquares("e5"));
        }
    }

}