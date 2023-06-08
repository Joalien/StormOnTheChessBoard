package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    @Test
    void spawn_knight() {
        Knight knight = new Knight(Color.WHITE);
        knight.setPosition(e4);


        assertEquals(File.E, knight.getFile());
        assertEquals(Row.Four, knight.getRow());
    }

    @Test
    void should_return_all_square_it_will_go_through() {
        Knight knight = new Knight(Color.WHITE);

        assertTrue(knight.squaresOnThePath(c2).isEmpty());
    }

    @Test
    void should_print_itself() {
        Knight knight = new Knight(Color.WHITE);

        assertEquals("white Knight", knight.toString());
    }

    @Nested
    class Movements {
        @Test
        void move_knight_in_the_middle_of_the_board() {
            List<Position> validMoves = Stream.of(g1, e1, d2, d4, e5, g5, h4, h2).toList();
            Knight knight = new Knight(Color.WHITE);
            knight.setPosition(f3);

            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == knight.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void move_knight_in_the_corner() {
            List<Position> validMoves = Stream.of(f2, g3).toList();
            Knight knight = new Knight(Color.WHITE);
            knight.setPosition(h1);

            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == knight.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Knight knight = new Knight(Color.WHITE);
            knight.setPosition(e5);

            assertFalse(knight.isPositionTheoreticallyReachable(e5));
        }
    }
}