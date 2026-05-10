package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.board.effect.FrenzyEffect;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FrenzyCardTest {

    ChessBoard board;
    FrenzyCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new FrenzyCard();
    }

    @Nested
    class PlayCard {
        @Test
        void should_add_frenzy_effect() {
            card.playOn(board, new NoCardParam());

            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof FrenzyEffect));
        }
    }

    @Nested
    class FrenzyActive {
        @BeforeEach
        void activateFrenzy() {
            card.playOn(board, new NoCardParam());
            board.setTurn(Color.WHITE);
        }

        @Test
        void should_allow_capture_move() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, a5);

            // White rook captures black pawn - should be allowed
            assertDoesNotThrow(() -> board.tryToMove(a1, a5));
            assertTrue(board.getOutOfTheBoardPieces().contains(blackPawn));
        }

        @Test
        void should_reject_non_capture_when_capture_available() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, a5);

            // White rook tries to move to a3 (non-capture) when a capture is available (a5)
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(a1, a3));
        }

        @Test
        void should_remove_effect_when_no_capture_available() {
            // No capturable pieces on the board (only kings, which can't be captured)
            // The effect should remove itself when a player has no captures

            // White has no captures available, effect should be removed before the move
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);

            // Move should succeed because frenzy is removed (no captures available)
            assertDoesNotThrow(() -> board.tryToMove(a1, a3));
            assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof FrenzyEffect));
        }

        @Test
        void should_force_black_to_capture_too() {
            // Set up a position where black has a capture available
            board.setTurn(Color.BLACK);
            Rock blackRook = new Rock(Color.BLACK);
            board.add(blackRook, a8);
            Pawn whitePawn = new Pawn(Color.WHITE);
            board.add(whitePawn, a2);

            // Black rook tries non-capture move when capture is available
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(a8, a5));
        }

        @Test
        void afterCardPlayHook_rejects_REPLACE_TURN_that_does_not_capture() {
            // Capture is available (rook on a1 vs pawn on a5).
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, a5);
            FrenzyEffect frenzy = (FrenzyEffect) board.getEffects().stream()
                    .filter(e -> e instanceof FrenzyEffect).findFirst().orElseThrow();

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> frenzy.afterCardPlayHook(board, fr.kubys.card.CardType.REPLACE_TURN));
            assertTrue(ex.getMessage().contains("Fringale"), "Message should name Fringale: " + ex.getMessage());
            assertTrue(ex.getMessage().contains("pièce"), "Message should mention the missed capture: " + ex.getMessage());
        }

        @Test
        void afterCardPlayHook_accepts_REPLACE_TURN_that_captures() {
            // Trigger a capture this turn first so lastMoveWasCapture() == true.
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, a5);
            board.tryToMove(a1, a5); // capture
            FrenzyEffect frenzy = (FrenzyEffect) board.getEffects().stream()
                    .filter(e -> e instanceof FrenzyEffect).findFirst().orElse(null);
            // Frenzy may auto-remove via beforeMoveHook if no further capture is reachable.
            // We invoke afterCardPlayHook on a still-attached instance to validate the
            // capture-aware branch.
            FrenzyEffect probe = frenzy != null ? frenzy : new FrenzyEffect(board);
            assertDoesNotThrow(() -> probe.afterCardPlayHook(board, fr.kubys.card.CardType.REPLACE_TURN));
        }

        @Test
        void afterCardPlayHook_ignores_BEFORE_TURN_and_AFTER_TURN() {
            FrenzyEffect frenzy = (FrenzyEffect) board.getEffects().stream()
                    .filter(e -> e instanceof FrenzyEffect).findFirst().orElseThrow();

            // No capture this turn, capture available, but BEFORE_TURN/AFTER_TURN aren't
            // policed by Frenzy (they go through other code paths that already enforce it).
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, a5);

            assertDoesNotThrow(() -> frenzy.afterCardPlayHook(board, fr.kubys.card.CardType.BEFORE_TURN));
            assertDoesNotThrow(() -> frenzy.afterCardPlayHook(board, fr.kubys.card.CardType.AFTER_TURN));
        }
    }
}
