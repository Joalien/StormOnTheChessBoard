package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.NonViolentEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class NonViolentCardTest {

    ChessBoard board;
    NonViolentCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new NonViolentCard();
    }

    @Nested
    class PlayCard {
        @Test
        void should_make_own_piece_non_violent() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, c3);

            card.playOn(board, new PieceCardParam(knight));

            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof NonViolentEffect));
        }

        @Test
        void should_reject_king() {
            King king = (King) board.at(e1).getPiece().get();

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(king)));
        }

        @Test
        void should_reject_enemy_piece() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, c6);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(knight)));
        }
    }

    @Nested
    class NonViolentActive {
        Knight nonViolentKnight;

        @BeforeEach
        void makeKnightNonViolent() {
            nonViolentKnight = new Knight(Color.WHITE);
            board.add(nonViolentKnight, c3);
            card.playOn(board, new PieceCardParam(nonViolentKnight));
            board.setTurn(Color.WHITE);
        }

        @Test
        void should_allow_non_violent_piece_to_move_to_empty_square() {
            assertDoesNotThrow(() -> board.tryToMove(c3, d5));
            assertEquals(nonViolentKnight, board.at(d5).getPiece().get());
        }

        @Test
        void should_prevent_non_violent_piece_from_being_captured() {
            board.setTurn(Color.BLACK);
            Rock blackRook = new Rock(Color.BLACK);
            board.add(blackRook, c8);

            // Black rook cannot capture the non-violent knight on c3
            assertThrows(Exception.class, () -> board.tryToMove(c8, c3));
        }

        @Test
        void should_prevent_non_violent_piece_from_capturing() {
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, d5);

            // Non-violent knight cannot capture the black pawn
            assertThrows(Exception.class, () -> board.tryToMove(c3, d5));
        }

        @Test
        void should_allow_non_violent_piece_to_give_check() {
            // Move non-violent knight to d6 which attacks e8 (black king)
            board.move(nonViolentKnight, d6);
            board.setTurn(Color.WHITE);

            // Non-violent piece can still give check
            assertTrue(board.isKingUnderAttack(Color.BLACK));
        }

        @Test
        void should_remove_effect_when_piece_is_removed_from_board() {
            board.removePieceFromTheBoard(nonViolentKnight);

            assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof NonViolentEffect));
        }
    }
}
