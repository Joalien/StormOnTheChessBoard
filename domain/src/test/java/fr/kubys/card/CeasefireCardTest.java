package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.CeasefireEffect;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CeasefireCardTest {

    ChessBoard board;
    CeasefireCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CeasefireCard();
    }

    @Nested
    class PlayCard {
        @Test
        void should_add_ceasefire_effect() {
            card.playOn(board, new NoCardParam());

            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof CeasefireEffect));
        }
    }

    @Nested
    class CeasefireActive {
        @BeforeEach
        void activateCeasefire() {
            card.playOn(board, new NoCardParam());
            board.setTurn(Color.WHITE);
        }

        @Test
        void should_prevent_capture() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, a5);

            // White rook cannot capture black pawn
            assertThrows(Exception.class, () -> board.tryToMove(a1, a5));
        }

        @Test
        void should_allow_move_to_empty_square() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);

            assertDoesNotThrow(() -> board.tryToMove(a1, a3));
            assertEquals(whiteRook, board.at(a3).getPiece().get());
        }

        @Test
        void should_prevent_pawn_capture() {
            Pawn whitePawn = new Pawn(Color.WHITE);
            board.add(whitePawn, d4);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, e5);

            // White pawn cannot capture diagonally
            assertThrows(Exception.class, () -> board.tryToMove(d4, e5));
        }

        @Test
        void should_allow_pawn_push() {
            Pawn whitePawn = new Pawn(Color.WHITE);
            board.add(whitePawn, d4);

            assertDoesNotThrow(() -> board.tryToMove(d4, d5));
        }

        @Test
        void should_remove_effect_when_king_is_checked() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);

            // Move rook to e-file to check black king
            board.move(whiteRook, a8);
            // After the move, the rook on a8 doesn't check e8 king

            // Re-setup: put rook where it checks the king directly
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.setTurn(Color.WHITE);
            card.playOn(board, new NoCardParam());
            board.setTurn(Color.WHITE);

            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a5);
            // Move rook to e5, checking black king on e8
            board.move(rook, e5);

            // Effect should be removed because black king is now in check
            assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof CeasefireEffect));
        }

        @Test
        void should_keep_effect_when_no_check() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a1);

            // Move rook to a3 (no check)
            board.move(whiteRook, a3);

            // Effect should still be active
            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof CeasefireEffect));
        }

        @Test
        void should_prevent_both_colors_from_capturing() {
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, d5);
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, d1);

            board.setTurn(Color.BLACK);
            // Black pawn cannot capture (no target anyway, but let's test with a target)
            Knight whiteKnight = new Knight(Color.WHITE);
            board.add(whiteKnight, c4);

            assertThrows(Exception.class, () -> board.tryToMove(d5, c4));
        }

        @Test
        void should_allow_card_effects_to_remove_pieces_during_ceasefire() {
            // SuicideKnightCard can still remove pieces via card effect even during ceasefire
            Knight whiteKnight = new Knight(Color.WHITE);
            board.add(whiteKnight, d5);
            Pawn blackPawn1 = new Pawn(Color.BLACK);
            Pawn blackPawn2 = new Pawn(Color.BLACK);
            board.add(blackPawn1, c7);
            board.add(blackPawn2, f6);

            SuicideKnightCard suicideKnightCard = new SuicideKnightCard();
            suicideKnightCard.playOn(board, new KnightCardParam(whiteKnight));

            assertTrue(board.getOutOfTheBoardPieces().contains(blackPawn1));
            assertTrue(board.getOutOfTheBoardPieces().contains(blackPawn2));
            assertTrue(board.getOutOfTheBoardPieces().contains(whiteKnight));
        }
    }
}
