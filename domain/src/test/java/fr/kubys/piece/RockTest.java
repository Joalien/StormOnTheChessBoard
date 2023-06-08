package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class RockTest {

    @Test
    void spawn_rock() {
        Rock rock = new Rock(Color.WHITE);
        rock.setSquare(new Square(e4));


        assertEquals(File.E, rock.getFile());
        assertEquals(Row.Four, rock.getRow());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            Rock rock = new Rock(Color.WHITE);
            rock.setSquare(new Square(c6));

            assertEquals(Collections.emptySet(), rock.squaresOnThePath(b6));
        }

        @Test
        void should_return_all_square_it_will_go_through() {
            Rock rock = new Rock(Color.WHITE);
            rock.setSquare(new Square(a1));

            assertEquals(Set.of(a2, a3, a4, a5, a6, a7), rock.squaresOnThePath(a8));
        }

        @Test
        void should_return_all_square_it_will_go_through_bis() {
            Rock rock = new Rock(Color.WHITE);
            rock.setSquare(new Square(e4));

            assertEquals(Set.of(b4, c4, d4), rock.squaresOnThePath(a4));
        }

        @Test
        void should_not_be_reachable() {
            Rock rock = new Rock(Color.WHITE);
            rock.setSquare(new Square(e4));

            assertTrue(rock.squaresOnThePath(b2).isEmpty());
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_on_cells_from_center() {
            Set<Position> validMoves = Set.of(e3, e2, e1, e5, e6, e7, e8, d4, c4, b4, a4, f4, g4, h4);
            Rock rock = new Rock(Color.WHITE);
            rock.setSquare(new Square(e4));


            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == rock.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void should_move_on_cells_from_corner() {
            Set<Position> validMoves = Set.of(a2, a3, a4, a5, a6, a7, a8, d1, c1, b1, e1, f1, g1, h1);
            Rock rock = new Rock(Color.WHITE);
            rock.setSquare(new Square(a1));


            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == rock.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Rock rock = new Rock(Color.WHITE);
            rock.setSquare(new Square(e5));

            assertFalse(rock.isPositionTheoreticallyReachable(e5));
        }
    }


}