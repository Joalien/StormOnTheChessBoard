package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.board.effect.*;
import fr.kubys.card.params.*;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.Crab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests combining multiple effects and cards to stress-test
 * the engine and uncover hidden bugs in effect interactions.
 */
class CombinedEffectsIT {

    ChessBoard board;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
    }

    // ========================================================================
    // Bombing + Magnetism interactions
    // ========================================================================
    @Nested
    class BombingAndMagnetism {

        @Test
        void bomb_should_destroy_magnetized_piece_and_remove_both_effects() {
            Knight magnetized = new Knight(Color.BLACK);
            board.add(magnetized, f6);
            board.addEffect(new MagnetismEffect(magnetized));
            board.addEffect(new BombingEffect(e4, Color.WHITE));
            board.setTurn(Color.BLACK);

            board.tryToMove(f6, e4);

            assertTrue(board.getOutOfTheBoardPieces().contains(magnetized));
            assertTrue(board.getEffects().isEmpty());
        }

        @Test
        void magnetism_should_prevent_piece_from_reaching_bomb() {
            Knight magnetized = new Knight(Color.BLACK);
            board.add(magnetized, d4);
            board.addEffect(new MagnetismEffect(magnetized));
            board.addEffect(new BombingEffect(c8, Color.BLACK));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, c4);

            assertThrows(MagnetismException.class, () -> board.tryToMove(c4, c8));
        }
    }

    // ========================================================================
    // BlackHole + ManHole interactions
    // ========================================================================
    @Nested
    class BlackHoleAndManHole {

        @Test
        void manhole_should_teleport_past_black_hole() {
            board.addEffect(new ManHoleEffect(a3, f6));
            board.addEffect(new BlackHoleEffect(b4));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a3);

            assertDoesNotThrow(() -> board.tryToMove(a3, f6));
            assertEquals(rook, board.at(f6).getPiece().get());
        }

        @Test
        void black_hole_on_manhole_exit_should_block_teleportation() {
            board.addEffect(new BlackHoleEffect(f6));
            board.addEffect(new ManHoleEffect(a3, f6));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a3);

            assertThrows(IllegalMoveException.class, () -> board.tryToMove(a3, f6));
        }
    }

    // ========================================================================
    // Barricade + ManHole interactions
    // ========================================================================
    @Nested
    class BarricadeAndManHole {

        @Test
        void manhole_should_bypass_barricade() {
            board.addEffect(new BarricadeEffect(d4, e4, d5, e5));
            board.addEffect(new ManHoleEffect(e3, e6));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, e3);

            assertDoesNotThrow(() -> board.tryToMove(e3, e6));
            assertEquals(rook, board.at(e6).getPiece().get());
        }
    }

    // ========================================================================
    // Bombing + Card interactions
    // ========================================================================
    @Nested
    class BombingAndCards {

        @Test
        void exile_card_should_trigger_bomb_on_enemy_landing() {
            Knight blackKnight = new Knight(Color.BLACK);
            board.add(blackKnight, d5);
            // Add bomb AFTER piece to avoid premature trigger
            board.addEffect(new BombingEffect(b8, Color.WHITE));

            new ExileCard().playOn(board, new PieceToPositionCardParam(blackKnight, b8));

            assertTrue(board.getOutOfTheBoardPieces().contains(blackKnight));
            assertTrue(board.getEffects().isEmpty());
        }

        @Test
        void swap_should_trigger_bomb_on_enemy_piece_landing() {
            Bishop whiteBishop = new Bishop(Color.WHITE);
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteBishop, c1);
            board.add(whiteRook, f3);
            board.addEffect(new BombingEffect(c1, Color.BLACK));

            new AsylumCard().playOn(board, new TwoPieceCardParam(whiteBishop, whiteRook));

            assertTrue(board.getOutOfTheBoardPieces().contains(whiteRook));
            assertEquals(whiteBishop, board.at(f3).getPiece().get());
            assertTrue(board.getEffects().isEmpty());
        }

        @Test
        void crab_on_bomb_should_be_destroyed() {
            Crab crab = new Crab(Color.BLACK);
            board.add(crab, c4);
            board.addEffect(new BombingEffect(d5, Color.WHITE));
            board.setTurn(Color.BLACK);

            board.tryToMove(c4, d5);

            assertTrue(board.getOutOfTheBoardPieces().contains(crab));
            assertTrue(board.getEffects().isEmpty());
        }
    }

    // ========================================================================
    // Multiple effects + piece transformations
    // ========================================================================
    @Nested
    class MultipleEffectsWithTransformations {

        @Test
        void neutralized_piece_should_still_trigger_bomb() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, d5);
            knight.setColor(Color.NONE);
            board.addEffect(new BombingEffect(f6, Color.BLACK));

            board.tryToMove(d5, f6);

            assertTrue(board.getOutOfTheBoardPieces().contains(knight));
            assertTrue(board.getEffects().isEmpty());
        }

        @Test
        void crab_card_then_move_crab_through_manhole() {
            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, d4);
            board.addEffect(new ManHoleEffect(d4, g7));

            new CrabCard().playOn(board, new PieceCardParam(pawn));
            Piece crab = board.at(d4).getPiece().get();
            assertInstanceOf(Crab.class, crab);

            assertDoesNotThrow(() -> board.tryToMove(d4, g7));
            assertEquals(crab, board.at(g7).getPiece().get());
        }

        @Test
        void disintegrate_piece_that_has_magnetism_should_remove_effect() {
            Knight magnetized = new Knight(Color.WHITE);
            board.add(magnetized, d4);
            board.addEffect(new MagnetismEffect(magnetized));

            new DisintegrationCard().playOn(board, new PieceCardParam(magnetized));

            assertTrue(board.getOutOfTheBoardPieces().contains(magnetized));
            assertTrue(board.getEffects().isEmpty());
        }
    }

    // ========================================================================
    // Three+ effects simultaneously
    // ========================================================================
    @Nested
    class ThreeOrMoreEffects {

        @Test
        void piece_should_navigate_around_black_hole_and_barricade() {
            board.addEffect(new BlackHoleEffect(d4));
            board.addEffect(new BarricadeEffect(e4, e5, d4, d5));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, e3);

            assertThrows(Exception.class, () -> board.tryToMove(e3, e5));
            assertDoesNotThrow(() -> board.tryToMove(e3, b3));
        }

        @Test
        void manhole_bomb_and_magnetism_all_active() {
            Knight magnetized = new Knight(Color.WHITE);
            board.add(magnetized, b4);
            board.addEffect(new ManHoleEffect(a3, f6));
            board.addEffect(new BombingEffect(f6, Color.BLACK));
            board.addEffect(new MagnetismEffect(magnetized));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a3);

            // Rook adjacent to magnetized knight → can't move
            assertThrows(MagnetismException.class, () -> board.tryToMove(a3, f6));

            // Move the knight → magnetism removed
            board.tryToMove(b4, c6);
            assertFalse(board.getEffects().stream().anyMatch(e -> e instanceof MagnetismEffect));

            // Now rook can teleport via manhole to f6 → bomb triggers
            board.setTurn(Color.WHITE);
            board.tryToMove(a3, f6);

            assertTrue(board.getOutOfTheBoardPieces().contains(rook));
            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof ManHoleEffect));
            assertFalse(board.getEffects().stream().anyMatch(e -> e instanceof BombingEffect));
        }

        @Test
        void four_effects_and_teleport_onto_bomb() {
            board.addEffect(new BarricadeEffect(d4, e4, d5, e5));
            board.addEffect(new BlackHoleEffect(c6));
            board.addEffect(new ManHoleEffect(b2, g7));
            board.addEffect(new BombingEffect(g7, Color.BLACK));

            assertEquals(4, board.getEffects().size());

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, b2);

            board.tryToMove(b2, g7);

            assertTrue(board.getOutOfTheBoardPieces().contains(rook));
            assertEquals(3, board.getEffects().size());
            assertFalse(board.getEffects().stream().anyMatch(e -> e instanceof BombingEffect));
        }
    }

    // ========================================================================
    // Card combos (playing multiple cards in sequence)
    // ========================================================================
    @Nested
    class CardCombos {

        @Test
        void shot_card_should_not_trigger_bomb() {
            // Place piece first, then bomb
            Knight enemy = new Knight(Color.BLACK);
            board.add(enemy, d5);
            board.addEffect(new BombingEffect(d5, Color.WHITE));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, d2);

            // Shot removes enemy without moving → bomb should NOT trigger
            new ShotCard().playOn(board, new PieceToPositionCardParam(rook, d5));

            assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
            assertEquals(rook, board.at(d2).getPiece().get());
            // Bomb is still active (not triggered by card removal)
            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof BombingEffect));
        }

        @Test
        void fright_should_respect_black_hole() {
            board.addEffect(new BlackHoleEffect(d6));

            Pawn pawn = new Pawn(Color.BLACK);
            board.add(pawn, d5);

            // d6 is blocked → reject
            assertThrows(IllegalArgumentException.class,
                    () -> new FrightCard().playOn(board, new PieceToPositionCardParam(pawn, d6)));

            // d7 is free → should work
            assertDoesNotThrow(
                    () -> new FrightCard().playOn(board, new PieceToPositionCardParam(pawn, d7)));
            assertEquals(pawn, board.at(d7).getPiece().get());
        }

        @Test
        void breakthrough_capture_onto_bomb_should_trigger() {
            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, d4);
            Knight enemy = new Knight(Color.BLACK);
            board.add(enemy, d5);
            board.addEffect(new BombingEffect(d5, Color.BLACK));

            new BreakthroughCard().playOn(board, new PieceCardParam(pawn));

            assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
            assertTrue(board.getOutOfTheBoardPieces().contains(pawn));
        }

        @Test
        void four_corners_with_black_hole_on_corner() {
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.setTurn(Color.WHITE);

            board.add(new Bishop(Color.WHITE), a1);
            board.add(new Bishop(Color.BLACK), h1);
            board.add(new Bishop(Color.BLACK), a8);
            board.addEffect(new BlackHoleEffect(h8));

            Knight knight = new Knight(Color.WHITE);
            board.add(knight, d4);

            assertThrows(IllegalArgumentException.class,
                    () -> new FourCornersCard().playOn(board, new PieceCardParam(knight)));
        }

        @Test
        void private_jet_should_respect_black_hole() {
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.setTurn(Color.WHITE);

            board.addEffect(new BlackHoleEffect(d5));

            assertThrows(IllegalArgumentException.class,
                    () -> new PrivateJetCard().playOn(board, new PositionCardParam(d5)));

            assertDoesNotThrow(
                    () -> new PrivateJetCard().playOn(board, new PositionCardParam(c5)));
        }

        @Test
        void cannibal_onto_magnetized_piece_should_remove_effect() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, d4);
            board.addEffect(new MagnetismEffect(knight));

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, d2);

            new CannibalCard().playOn(board, new PieceToPositionCardParam(rook, d4));

            assertTrue(board.getOutOfTheBoardPieces().contains(knight));
            assertEquals(rook, board.at(d4).getPiece().get());
            assertTrue(board.getEffects().isEmpty());
        }
    }

    // ========================================================================
    // Promotion edge cases with effects
    // ========================================================================
    @Nested
    class PromotionWithEffects {

        @Test
        void pawn_promoted_via_manhole_teleportation() {
            board.addEffect(new ManHoleEffect(d6, d8));

            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, d6);

            board.tryToMove(d6, d8);

            assertInstanceOf(Queen.class, board.at(d8).getPiece().get());
            assertEquals(Color.WHITE, board.at(d8).getPiece().get().getColor());
        }

        @Test
        void banzai_three_squares_onto_bomb() {
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), a1);
            board.add(new King(Color.BLACK), h8);
            board.setTurn(Color.WHITE);

            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, d2);
            board.addEffect(new BombingEffect(d5, Color.BLACK));

            new BanzaiCard().playOn(board, new PieceCardParam(pawn));

            assertTrue(board.getOutOfTheBoardPieces().contains(pawn));
            assertTrue(board.getEffects().isEmpty());
        }

        @Test
        void banzai_to_promotion_rank() {
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), a1);
            board.add(new King(Color.BLACK), h8);
            board.setTurn(Color.WHITE);

            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, d5);

            new BanzaiCard().playOn(board, new PieceCardParam(pawn));

            assertInstanceOf(Queen.class, board.at(d8).getPiece().get());
        }
    }
}
