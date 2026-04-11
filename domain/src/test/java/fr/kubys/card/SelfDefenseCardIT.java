package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class SelfDefenseCardIT {

    public static final NoCardParam NO_CARD_PARAM = new NoCardParam();
    ChessBoard board;
    SelfDefenseCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        card = new SelfDefenseCard();
    }

    @Nested
    class Success {
        @Test
        void pawn_captures_knight_then_self_defense_restores_knight_and_removes_pawn() {
            Pawn pawn = new Pawn(Color.WHITE);
            Knight blackKnight = new Knight(Color.BLACK);
            board.add(pawn, d4);
            board.add(blackKnight, e5);

            board.move(pawn, e5); // white pawn captures black knight

            // Black plays self defense
            card.playOn(board, NO_CARD_PARAM);

            // Black knight is restored, white pawn is eliminated
            Piece restored = board.at(e5).getPiece().orElseThrow();
            assertSame(blackKnight, restored);
            assertEquals(Color.BLACK, restored.getColor());
            assertTrue(board.getOutOfTheBoardPieces().contains(pawn));
        }

        @Test
        void rook_captures_bishop_then_self_defense() {
            Rock whiteRook = new Rock(Color.WHITE);
            Bishop blackBishop = new Bishop(Color.BLACK);
            board.add(whiteRook, d1);
            board.add(blackBishop, d5);

            board.move(whiteRook, d5);

            card.playOn(board, new NoCardParam());

            Piece restored = board.at(d5).getPiece().orElseThrow();
            assertSame(blackBishop, restored);
            assertTrue(board.getOutOfTheBoardPieces().contains(whiteRook));
        }

        @Test
        void black_attacks_white_and_white_uses_self_defense() {
            board.setTurn(Color.BLACK);
            Knight blackKnight = new Knight(Color.BLACK);
            Queen whiteQueen = new Queen(Color.WHITE);
            board.add(blackKnight, e5);
            board.add(whiteQueen, d3);

            board.move(blackKnight, d3); // black knight captures white queen

            // White plays self defense
            card.playOn(board, new NoCardParam());

            Piece restored = board.at(d3).getPiece().orElseThrow();
            assertSame(whiteQueen, restored);
            assertTrue(board.getOutOfTheBoardPieces().contains(blackKnight));
        }
    }

    @Nested
    class Promotion {
        @Test
        void self_defense_before_promotion_choice_removes_auto_promoted_queen() {
            // White pawn on 7th rank captures black piece on 8th rank → auto-promotes to Queen
            Pawn pawn = new Pawn(Color.WHITE);
            Rock blackRook = new Rock(Color.BLACK);
            board.add(pawn, e7);
            board.add(blackRook, f8);

            board.move(pawn, f8); // capture + auto-promote to Queen

            // Verify auto-promotion happened
            Piece promoted = board.at(f8).getPiece().orElseThrow();
            assertInstanceOf(Queen.class, promoted);
            assertEquals(Color.WHITE, promoted.getColor());

            // Black plays self defense BEFORE white chooses promotion piece
            card.playOn(board, NO_CARD_PARAM);

            // Black rook is restored, white promoted queen is eliminated
            Piece restored = board.at(f8).getPiece().orElseThrow();
            assertSame(blackRook, restored);
            assertEquals(Color.BLACK, restored.getColor());
        }

        @Test
        void self_defense_after_promotion_choice_removes_chosen_piece() {
            // White pawn on 7th rank captures black piece on 8th rank → auto-promotes to Queen
            Pawn pawn = new Pawn(Color.WHITE);
            Knight blackKnight = new Knight(Color.BLACK);
            board.add(pawn, d7);
            board.add(blackKnight, e8);

            board.move(pawn, e8); // capture + auto-promote to Queen

            // White overrides promotion to a Rook
            board.overridePromotion(e8, PromotionPiece.ROOK);
            Piece overridden = board.at(e8).getPiece().orElseThrow();
            assertInstanceOf(Rock.class, overridden);

            // Black plays self defense AFTER white chose promotion piece
            card.playOn(board, NO_CARD_PARAM);

            // Black knight is restored, white rook (chosen promotion) is eliminated
            Piece restored = board.at(e8).getPiece().orElseThrow();
            assertSame(blackKnight, restored);
            assertEquals(Color.BLACK, restored.getColor());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_fail_if_no_capture_this_turn() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, d1);

            board.move(whiteRook, d5); // move without capture

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new NoCardParam()));
        }
    }
}
