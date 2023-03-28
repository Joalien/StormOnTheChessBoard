import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

class KnightTest {

    @Test
    void spawn_knight() {
        Knight knight = new Knight("e4", Color.WHITE);

        Assertions.assertEquals(5, knight.x);
        Assertions.assertEquals(4, knight.y);
    }

    @Test
    void should_return_all_square_it_will_go_through() {
        Knight knight = new Knight("a1", Color.WHITE);

        Assertions.assertTrue(knight.squaresOnThePath("c2").isEmpty());
    }

    @Nested
    class Movements {
        @Test
        void move_knight_in_the_middle_of_the_board() {
            List<String> validMoves = List.of("g1", "e1", "d2", "d4", "e5", "g5", "h4", "h2");
            Knight knight = new Knight("f3", Color.WHITE);

            Assertions.assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .peek(s -> System.out.println(s + " " + validMoves.contains(s) + " " + knight.reachableSquares(s)))
                    .allMatch(s -> validMoves.contains(s) == knight.reachableSquares(s)));
        }

        @Test
        void move_knight_in_the_corner() {
            List<String> validMoves = List.of("f2", "g3");
            Knight knight = new Knight("h1", Color.WHITE);

            Assertions.assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .peek(s -> System.out.println(s + " " + validMoves.contains(s) + " " + knight.reachableSquares(s)))
                    .allMatch(s -> validMoves.contains(s) == knight.reachableSquares(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Knight knight = new Knight("e5", Color.WHITE);

            Assertions.assertFalse(knight.reachableSquares("e5"));
        }
    }

}