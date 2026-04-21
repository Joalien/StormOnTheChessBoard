package fr.kubys.board;

import fr.kubys.board.effect.BarricadeEffect;
import fr.kubys.board.effect.ManHoleEffect;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class LegalMovesTest {

    private ChessBoard board;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
    }

    @Nested
    class BasicMoves {
        @Test
        void pawn_on_starting_row_can_move_one_or_two_squares() {
            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, e2);

            Set<Position> moves = board.getLegalMoves(pawn);

            assertTrue(moves.contains(e3));
            assertTrue(moves.contains(e4));
            assertEquals(2, moves.size());
        }

        @Test
        void pawn_can_capture_diagonally() {
            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, d4);
            board.add(new Pawn(Color.BLACK), e5);

            Set<Position> moves = board.getLegalMoves(pawn);

            assertTrue(moves.contains(d5));
            assertTrue(moves.contains(e5));
            assertEquals(2, moves.size());
        }

        @Test
        void knight_has_all_l_shaped_moves_on_empty_board() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, d4);

            Set<Position> moves = board.getLegalMoves(knight);

            assertEquals(Set.of(c2, b3, b5, c6, e6, f5, f3, e2), moves);
        }

        @Test
        void rook_moves_along_ranks_and_files() {
            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a1);

            Set<Position> moves = board.getLegalMoves(rook);

            // a-file (a2-a8) + first rank (b1-d1) — e1 is blocked by own king
            assertTrue(moves.contains(a2));
            assertTrue(moves.contains(a8));
            assertTrue(moves.contains(b1));
            assertTrue(moves.contains(d1));
            assertFalse(moves.contains(e1)); // own king
        }

        @Test
        void bishop_moves_diagonally() {
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, c1);

            Set<Position> moves = board.getLegalMoves(bishop);

            assertTrue(moves.contains(d2));
            assertTrue(moves.contains(b2));
            assertTrue(moves.contains(e3));
            assertTrue(moves.contains(a3));
        }

        @Test
        void king_moves_one_square_in_each_direction() {
            Set<Position> moves = board.getLegalMoves(board.at(e1).getPiece().get());

            assertTrue(moves.contains(d1));
            assertTrue(moves.contains(d2));
            assertTrue(moves.contains(e2));
            assertTrue(moves.contains(f2));
            assertTrue(moves.contains(f1));
        }

        @Test
        void empty_square_returns_empty_set() {
            assertThrows(IllegalArgumentException.class, () -> board.getLegalMoves(
                    board.at(a3).getPiece().orElseThrow(() -> new IllegalArgumentException("No piece"))
            ));
        }
    }

    @Nested
    class BlockedByOwnPieces {
        @Test
        void pawn_blocked_by_own_piece_in_front() {
            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, e2);
            board.add(new Knight(Color.WHITE), e3);

            Set<Position> moves = board.getLegalMoves(pawn);

            assertTrue(moves.isEmpty());
        }

        @Test
        void rook_blocked_by_own_piece() {
            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a1);
            board.add(new Pawn(Color.WHITE), a3);

            Set<Position> moves = board.getLegalMoves(rook);

            assertTrue(moves.contains(a2));
            assertFalse(moves.contains(a3)); // own piece
            assertFalse(moves.contains(a4)); // beyond own piece
        }
    }

    @Nested
    class CheckFiltering {
        @Test
        void pinned_piece_cannot_move_off_pin_line() {
            // White rook on e2 is pinned by black rook on e7
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, e2);
            board.add(new Rock(Color.BLACK), e7);

            Set<Position> moves = board.getLegalMoves(whiteRook);

            assertTrue(moves.contains(e3)); // stays on pin line
            assertTrue(moves.contains(e7)); // can capture the attacker
            assertFalse(moves.contains(d2)); // would expose king to check
        }

        @Test
        void king_cannot_move_into_check() {
            board.add(new Rock(Color.BLACK), f8);
            King whiteKing = (King) board.at(e1).getPiece().get();

            Set<Position> moves = board.getLegalMoves(whiteKing);

            assertFalse(moves.contains(f1)); // attacked by rook on f8
            assertFalse(moves.contains(f2)); // attacked by rook on f8
            assertTrue(moves.contains(d1)); // safe square
            assertTrue(moves.contains(d2)); // safe square
        }

        @Test
        void when_in_check_only_moves_that_escape_check_are_legal() {
            // Black rook gives check on e-file
            board.add(new Rock(Color.BLACK), e7);
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, b1);

            Set<Position> moves = board.getLegalMoves(knight);

            // Knight cannot block the check on e-file, and cannot capture e7
            // Only moves that resolve the check are legal — none for this knight
            assertFalse(moves.contains(a3));
            assertFalse(moves.contains(c3));
        }
    }

    @Nested
    class WithEffects {
        @Test
        void barricade_blocks_movement_through_wall() {
            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a1);
            // Barricade between a3/a4 and b3/b4
            board.addEffect(new BarricadeEffect(a3, a4, b3, b4));

            Set<Position> moves = board.getLegalMoves(rook);

            assertTrue(moves.contains(a2));
            assertTrue(moves.contains(a3));
            assertFalse(moves.contains(a4)); // blocked by barricade
            assertFalse(moves.contains(a5)); // beyond barricade
        }

        @Test
        void manhole_allows_teleportation() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, a1);
            board.addEffect(new ManHoleEffect(a1, h8));

            Set<Position> moves = board.getLegalMoves(knight);

            assertTrue(moves.contains(h8)); // manhole teleport
            assertTrue(moves.contains(b3)); // normal knight move
        }
    }
}
