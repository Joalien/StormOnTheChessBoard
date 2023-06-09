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

class BishopTest {

    @Test
    void spawn_bishop() {
        Bishop bishop = new Bishop(Color.WHITE);
        bishop.setPosition(e4);

        assertEquals(File.E, bishop.getFile());
        assertEquals(Row.Four, bishop.getRow());
        assertEquals(e4, bishop.getPosition());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(c6);

            assertEquals(Collections.emptySet(), bishop.squaresOnThePath(b5));
        }

        @Test
        void should_return_all_square_it_will_go_through() {
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(a1);

            assertEquals(Set.of(b2, c3, d4, e5), bishop.squaresOnThePath(f6));
        }

        @Test
        void should_return_all_square_it_will_go_through_bis() {
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(e4);

            assertEquals(Set.of(d3), bishop.squaresOnThePath(c2));
        }

        @Test
        void should_return_all_square_it_will_go_through_ter() {
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(h1);

            assertEquals(Set.of(b7, c6, d5, e4, f3, g2), bishop.squaresOnThePath(a8));
        }

        @Test
        void should_not_be_reachable() {
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(a8);

            assertTrue(bishop.squaresOnThePath(a2).isEmpty());
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_on_cells_from_center() {
            Set<Position> validMoves = Set.of(f3, g2, h1, d5, c6, b7, a8, d3, c2, b1, f5, g6, h7);
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(e4);

            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == bishop.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void should_move_on_cells_from_corner() {
            Set<Position> validMoves = Set.of(b2, c3, d4, e5, f6, g7, h8);
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(a1);

            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == bishop.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            Bishop bishop = new Bishop(Color.WHITE);
            bishop.setPosition(e5);


            assertFalse(bishop.isPositionTheoreticallyReachable(e5));
        }
    }


}