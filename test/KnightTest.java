import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class KnightTest {

    @Test
    void spawn_knight() {
        Knight knight = new Knight(5, 4, true, 'K');

        Assertions.assertEquals(5, knight.positionSurLigne);
        Assertions.assertEquals(4, knight.positionSurColonne);
    }

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
}