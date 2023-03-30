package piece;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import position.PositionUtil;
import position.Square;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {

    @Test
    void spawn_king() {
        King king = new King(Color.WHITE);
        king.setSquare(new Square("e4"));


        assertEquals(5, king.getX());
        assertEquals(4, king.getY());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            King king = new King(Color.WHITE);
            king.setSquare(new Square("c6"));

            assertEquals(Collections.emptySet(), king.squaresOnThePath("b5"));
        }

        @Test
        void should_return_square_it_will_go_through_when_white_king_side_castling() {
            King king = new King(Color.WHITE);
            king.setSquare(new Square("e1"));

            assertEquals(Set.of("f1"), king.squaresOnThePath("g1"));
        }

        @Test
        void should_return_square_it_will_go_through_when_white_queen_side_castling() {
            King king = new King(Color.WHITE);
            king.setSquare(new Square("e1"));

            assertEquals(Set.of("d1"), king.squaresOnThePath("c1"));
        }

        @Test
        void should_return_square_it_will_go_through_when_black_king_side_castling() {
            King king = new King(Color.BLACK);
            king.setSquare(new Square("e8"));

            assertEquals(Set.of("f8"), king.squaresOnThePath("g8"));
        }

        @Test
        void should_return_square_it_will_go_through_when_black_queen_side_castling() {
            King king = new King(Color.BLACK);
            king.setSquare(new Square("e8"));

            assertEquals(Set.of("d8"), king.squaresOnThePath("c8"));
        }

        @Test
        void should_not_be_reachable() {
            King king = new King(Color.WHITE);
            king.setSquare(new Square("a1"));

            assertThrows(IllegalArgumentException.class, () -> king.squaresOnThePath("e8"));
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_on_cells_from_center() {
            Set<String> validMoves = Set.of("e5", "d5", "d4", "d3", "e3", "f3", "f4", "f5");
            King king = new King(Color.WHITE);
            king.setSquare(new Square("e4"));


            assertTrue(PositionUtil.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == king.reachableSquares(s)));
        }

        @Test
        void should_move_on_cells_from_corner() {
            Set<String> validMoves = Set.of("a2", "b2", "b1");
            King king = new King(Color.WHITE);
            king.setSquare(new Square("a1"));

            assertTrue(PositionUtil.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == king.reachableSquares(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            King king = new King(Color.WHITE);
            king.setSquare(new Square("e5"));

            assertFalse(king.reachableSquares("e5"));
        }

        @Nested
        class Castle {
            @Test
            void should_be_able_to_castle() {
                King king = new King(Color.WHITE);
                king.setSquare(new Square("e1"));

                assertTrue(king.reachableSquares("g1"));
            }

            @Test
            void should_not_be_able_to_castle_if_king_has_moved() {
                King king = new King(Color.WHITE);
                king.setSquare(new Square("e1"));
                king.cannotCastleAnymore();

                assertFalse(king.reachableSquares("g1"));
            }
        }
    }


}