package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.HideoutEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class HideoutCardTest {

    ChessBoard board;
    HideoutCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new HideoutCard();
    }

    @Nested
    class PlayCard {
        @Test
        void should_freeze_enemy_piece() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, c6);

            card.playOn(board, new PieceCardParam(knight));

            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof HideoutEffect));
        }

        @Test
        void should_reject_king() {
            King king = (King) board.at(e8).getPiece().get();

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(king)));
        }

        @Test
        void should_reject_own_piece() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, c3);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(knight)));
        }
    }

    @Nested
    class HideoutActive {
        Knight frozenKnight;

        @BeforeEach
        void freezeKnight() {
            frozenKnight = new Knight(Color.BLACK);
            board.add(frozenKnight, c6);
            card.playOn(board, new PieceCardParam(frozenKnight));
        }

        @Test
        void should_prevent_frozen_piece_from_moving() {
            board.setTurn(Color.BLACK);

            assertThrows(IllegalStateException.class,
                    () -> board.tryToMove(c6, d4));
        }

        @Test
        void should_allow_other_pieces_to_move() {
            board.setTurn(Color.BLACK);
            Rock blackRook = new Rock(Color.BLACK);
            board.add(blackRook, a8);

            assertDoesNotThrow(() -> board.tryToMove(a8, a5));
        }

        @Test
        void should_wake_up_when_own_king_is_checked() {
            // Put black king in check by moving white rook to e-file
            board.setTurn(Color.WHITE);
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a5);

            // Move rook to e5 - this checks the black king on e8
            board.move(whiteRook, e5);

            // Effect should be removed
            assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof HideoutEffect));
        }

        @Test
        void should_stay_frozen_when_no_check() {
            board.setTurn(Color.WHITE);
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, a5);

            // Move rook to a3 - no check
            board.move(whiteRook, a3);

            // Effect should still be active
            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof HideoutEffect));
        }

        @Test
        void should_not_give_check_when_frozen() {
            // Frozen knight on c6 normally would control e7, d4, etc.
            // Place white king on d4 where the frozen knight would attack
            board.removePieceFromTheBoard(board.at(e1).getPiece().get());
            board.add(new King(Color.WHITE), d4);
            board.setTurn(Color.WHITE);

            // Frozen piece should NOT threaten d4, so white king is NOT in check
            assertFalse(board.isKingUnderAttack(Color.WHITE));
        }

        @Test
        void should_allow_king_to_move_to_square_controlled_by_frozen_piece() {
            // Frozen knight on c6 controls b4
            // White king should be able to move to b4 since the frozen piece can't attack
            board.removePieceFromTheBoard(board.at(e1).getPiece().get());
            board.add(new King(Color.WHITE), a3);
            board.setTurn(Color.WHITE);

            // b4 is controlled by frozen knight on c6, but since it's frozen, king can move there
            assertDoesNotThrow(() -> board.tryToMove(a3, b4));
        }

        @Test
        void should_remove_effect_when_frozen_piece_is_captured() {
            board.removePieceFromTheBoard(frozenKnight);

            assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof HideoutEffect));
        }
    }
}
