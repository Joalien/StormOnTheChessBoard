package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.board.effect.BarricadeEffect;
import fr.kubys.card.params.BarricadeCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BarricadeCardTest {

    ChessBoard board;
    BarricadeCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        card = new BarricadeCard();
    }

    @Nested
    class Validation {
        @Test
        void should_reject_non_adjacent_edge() {
            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new BarricadeCardParam(d4, e6, d5, e5)));
        }

        @Test
        void should_reject_non_connected_edges() {
            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new BarricadeCardParam(d4, e4, d6, e6)));
        }

        @Test
        void should_accept_straight_barricade() {
            assertDoesNotThrow(() -> card.playOn(board, new BarricadeCardParam(d4, e4, d5, e5)));
            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof BarricadeEffect));
        }

        @Test
        void should_accept_l_shaped_barricade() {
            assertDoesNotThrow(() -> card.playOn(board, new BarricadeCardParam(e4, d4, d4, d3)));
            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof BarricadeEffect));
        }

        @Test
        void should_accept_l_shaped_barricade_alternate_order() {
            assertDoesNotThrow(() -> card.playOn(board, new BarricadeCardParam(d4, d3, d4, e4)));
        }

        @Test
        void should_accept_l_shaped_barricade_from_front_order() {
            // User selects: d4|e4 then d4|d3 (from1=d4, to1=e4, from2=d4, to2=d3)
            card.playOn(board, new BarricadeCardParam(d4, e4, d4, d3));

            // Should block orthogonal d4↔e4
            Rock rock = new Rock(Color.WHITE);
            board.add(rock, d4);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(rock, e4));
        }
    }

    @Nested
    class StraightBarricade {
        // Barricade between d-file and e-file at rows 4-5
        // Edges: d4|e4 and d5|e5

        @BeforeEach
        void placeBarricade() {
            card.playOn(board, new BarricadeCardParam(d4, e4, d5, e5));
        }

        @Test
        void should_block_rook_crossing_barricade() {
            Rock rock = new Rock(Color.WHITE);
            board.add(rock, d4);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(rock, e4));
        }

        @Test
        void should_block_rook_crossing_barricade_through_path() {
            Rock rock = new Rock(Color.WHITE);
            board.add(rock, a4);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(rock, h4));
        }

        @Test
        void should_not_block_rook_moving_parallel() {
            Rock rock = new Rock(Color.WHITE);
            board.add(rock, d1);
            assertDoesNotThrow(() -> board.tryToMove(rock, d8));
        }

        @Test
        void should_block_bishop_diagonal_crossing() {
            // Bishop from d4 to e5 crosses the barricade
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, d4);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(bishop, e5));
        }

        @Test
        void should_block_bishop_diagonal_crossing_through_path() {
            // Bishop from c3 to f6: path includes d4, e5 — crosses barricade
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, c3);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(bishop, f6));
        }

        @Test
        void should_not_block_bishop_not_crossing() {
            // Bishop from c3 to a5: doesn't cross the barricade
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, c3);
            assertDoesNotThrow(() -> board.tryToMove(bishop, a5));
        }

        @Test
        void should_allow_knight_to_cross() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, d4);
            assertDoesNotThrow(() -> board.tryToMove(knight, e6));
        }

        @Test
        void should_allow_piece_to_reach_square_just_before_barricade() {
            Rock rock = new Rock(Color.WHITE);
            board.add(rock, a4);
            assertDoesNotThrow(() -> board.tryToMove(rock, d4));
        }

        @Test
        void should_block_queen_diagonal_crossing() {
            Queen queen = new Queen(Color.WHITE);
            board.add(queen, e4);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(queen, d5));
        }
    }

    @Nested
    class LShapedBarricade {
        // Barricade: e4|d4 + d4|d3  (L-shape at d4 corner)
        // Blocks: d4↔e4, d4↔d3 (orthogonal), d4↔e3 (diagonal)
        // Does NOT block: d3↔e4

        @BeforeEach
        void placeBarricade() {
            card.playOn(board, new BarricadeCardParam(e4, d4, d4, d3));
        }

        @Test
        void should_block_orthogonal_edge1() {
            Rock rock = new Rock(Color.WHITE);
            board.add(rock, d4);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(rock, e4));
        }

        @Test
        void should_block_orthogonal_edge2() {
            Rock rock = new Rock(Color.WHITE);
            board.add(rock, d3);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(rock, d4));
        }

        @Test
        void should_block_diagonal_through_elbow() {
            // d4→e3 crosses the L-wall
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, d4);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(bishop, e3));
        }

        @Test
        void should_block_diagonal_through_elbow_on_path() {
            // c5→f2 passes through d4 and e3 — crosses the L-wall
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, c5);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(bishop, f2));
        }

        @Test
        void should_not_block_opposite_diagonal() {
            // d3→e4 passes through the opening of the L — not blocked
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, d3);
            assertDoesNotThrow(() -> board.tryToMove(bishop, e4));
        }

        @Test
        void should_not_block_opposite_diagonal_on_path() {
            // c2→f5 passes through d3 and e4 — goes through the opening
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, c2);
            assertDoesNotThrow(() -> board.tryToMove(bishop, f5));
        }

        @Test
        void should_allow_knight_to_cross() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, d4);
            assertDoesNotThrow(() -> board.tryToMove(knight, e2));
        }
    }

    @Nested
    class CheckProtection {
        @Test
        void king_should_not_be_in_check_behind_straight_barricade() {
            // Barricade between d and e files at rows 4-5
            card.playOn(board, new BarricadeCardParam(d4, e4, d5, e5));
            board.add(new King(Color.WHITE), d4);
            board.add(new King(Color.BLACK), a8);
            Rock enemyRook = new Rock(Color.BLACK);
            board.add(enemyRook, h4);

            // Rook on h4 would normally check king on d4 through e4,
            // but barricade blocks the path
            assertFalse(board.isKingUnderAttack(Color.WHITE));
        }

        @Test
        void king_should_not_be_in_check_behind_straight_barricade_diagonal() {
            card.playOn(board, new BarricadeCardParam(d4, e4, d5, e5));
            board.add(new King(Color.WHITE), d4);
            board.add(new King(Color.BLACK), a8);
            Bishop enemyBishop = new Bishop(Color.BLACK);
            board.add(enemyBishop, g7);

            // Bishop on g7 would normally check king on d4 through e5,
            // but barricade blocks the diagonal
            assertFalse(board.isKingUnderAttack(Color.WHITE));
        }

        @Test
        void king_should_still_be_in_check_from_same_side_of_barricade() {
            card.playOn(board, new BarricadeCardParam(d4, e4, d5, e5));
            board.add(new King(Color.WHITE), c4);
            board.add(new King(Color.BLACK), a8);
            Rock enemyRook = new Rock(Color.BLACK);
            board.add(enemyRook, a4);

            // Rook on a4 attacks king on c4 along row 4 — barricade is between d and e, doesn't help
            assertTrue(board.isKingUnderAttack(Color.WHITE));
        }

        @Test
        void king_should_not_be_in_check_behind_l_shaped_barricade() {
            // L-barricade: e4|d4 + d4|d3
            card.playOn(board, new BarricadeCardParam(e4, d4, d4, d3));
            board.add(new King(Color.WHITE), d4);
            board.add(new King(Color.BLACK), a8);
            Rock enemyRook = new Rock(Color.BLACK);
            board.add(enemyRook, h4);

            // Rook on h4 blocked by barricade edge d4|e4
            assertFalse(board.isKingUnderAttack(Color.WHITE));
        }

        @Test
        void king_should_not_be_in_check_behind_l_shaped_barricade_diagonal() {
            // L-barricade: e4|d4 + d4|d3 — blocks diagonal d4↔e3
            card.playOn(board, new BarricadeCardParam(e4, d4, d4, d3));
            board.add(new King(Color.WHITE), d4);
            board.add(new King(Color.BLACK), a8);
            Bishop enemyBishop = new Bishop(Color.BLACK);
            board.add(enemyBishop, f2);

            // Bishop on f2 would check d4 through e3, but barricade blocks d4↔e3
            assertFalse(board.isKingUnderAttack(Color.WHITE));
        }

        @Test
        void knight_should_still_check_king_through_barricade() {
            card.playOn(board, new BarricadeCardParam(d4, e4, d5, e5));
            board.add(new King(Color.WHITE), d4);
            board.add(new King(Color.BLACK), a8);
            Knight enemyKnight = new Knight(Color.BLACK);
            board.add(enemyKnight, e6);

            // Knight jumps over the barricade
            assertTrue(board.isKingUnderAttack(Color.WHITE));
        }
    }
}
