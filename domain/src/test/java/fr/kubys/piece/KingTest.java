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

class KingTest {

    @Test
    void spawn_king() {
        King king = new King(Color.WHITE);
        king.setPosition(e4);


        assertEquals(File.E, king.getFile());
        assertEquals(Row.Four, king.getRow());
    }

    @Nested
    class SquaresOnThePath {
        @Test
        void should_return_empty_square_it_will_go_through() {
            King king = new King(Color.WHITE);
            king.setPosition(c6);

            assertEquals(Collections.emptySet(), king.squaresOnThePath(b5));
        }

        @Test
        void should_not_be_reachable() {
            King king = new King(Color.WHITE);
            king.setPosition(a1);

            assertTrue(king.squaresOnThePath(e8).isEmpty());
        }
    }

    @Nested
    class Movements {
        @Test
        void should_move_on_cells_from_center() {
            Set<Position> validMoves = Set.of(e5, d5, d4, d3, e3, f3, f4, f5);
            King king = new King(Color.WHITE);
            king.setPosition(e4);


            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == king.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void should_move_on_cells_from_corner() {
            Set<Position> validMoves = Set.of(a2, b2, b1);
            King king = new King(Color.WHITE);
            king.setPosition(a1);

            assertTrue(Position.generateAllPositions()
                    .stream()
                    .allMatch(s -> validMoves.contains(s) == king.isPositionTheoreticallyReachable(s)));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            King king = new King(Color.WHITE);
            king.setPosition(e5);

            assertFalse(king.isPositionTheoreticallyReachable(e5));
        }

        @Test
        void should_not_reach_castle_target_by_movement_alone() {
            King king = new King(Color.WHITE);
            king.setPosition(e1);

            // Castle targets are not reachable by King's own movement (distance > 1)
            // Castling is handled by ChessBoard, not by King's movement logic
            assertFalse(king.isPositionTheoreticallyReachable(g1));
            assertFalse(king.isPositionTheoreticallyReachable(c1));
        }
    }


}