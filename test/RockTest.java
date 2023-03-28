import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

class RockTest {

    @Test
    void spawn_rock() {
        Rock rock = new Rock("e4", Color.WHITE);

        Assertions.assertEquals(5, rock.x);
        Assertions.assertEquals(4, rock.y);
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            Rock rock = new Rock("c6", Color.WHITE);

            Assertions.assertEquals(Collections.emptySet(), rock.squaresOnThePath("b6"));
        }

        @Test
        void should_return_all_square_it_will_go_through() {
            Rock rock = new Rock("a1", Color.WHITE);

            Assertions.assertEquals(Set.of("a2", "a3", "a4", "a5", "a6", "a7"), rock.squaresOnThePath("a8"));
        }

        @Test
        void should_return_all_square_it_will_go_through_bis() {
            Rock rock = new Rock("e4", Color.WHITE);

            Assertions.assertEquals(Set.of("b4", "c4", "d4"), rock.squaresOnThePath("a4"));
        }

        @Test
        void should_not_be_reachable() {
            Rock rock = new Rock("a1", Color.WHITE);

            Assertions.assertThrows(IllegalArgumentException.class, () -> rock.squaresOnThePath("b2"));
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_on_cells_from_center() {
            Set<String> validMoves = Set.of("e3", "e2", "e1", "e5", "e6", "e7", "e8", "d4", "c4", "b4", "a4", "f4", "g4", "h4");
            Rock rock = new Rock("e4", Color.WHITE);

            Assertions.assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .peek(s -> System.out.println(s + " " + validMoves.contains(s) + " " + rock.reachableSquares(s)))
                    .allMatch(s -> validMoves.contains(s) == rock.reachableSquares(s)));
        }

        @Test
        void should_move_on_cells_from_corner() {
            Set<String> validMoves = Set.of("a2", "a3", "a4", "a5", "a6", "a7", "a8", "d1", "c1", "b1", "e1", "f1", "g1", "h1");
            Rock rock = new Rock("a1", Color.WHITE);

            Assertions.assertTrue(BoardUtil.generateAllSquares()
                    .stream()
                    .peek(s -> System.out.println(s + " " + validMoves.contains(s) + " " + rock.reachableSquares(s)))
                    .allMatch(s -> validMoves.contains(s) == rock.reachableSquares(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Rock rock = new Rock("e5", Color.WHITE);

            Assertions.assertFalse(rock.reachableSquares("e5"));
        }
    }


}