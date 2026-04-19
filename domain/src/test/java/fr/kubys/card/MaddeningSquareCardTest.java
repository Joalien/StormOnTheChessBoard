package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.MaddeningSquareEffect;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class MaddeningSquareCardTest {

    ChessBoard board;
    MaddeningSquareCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new MaddeningSquareCard();
    }

    @Nested
    class PlayCard {
        @Test
        void should_add_maddening_square_effect() {
            card.playOn(board, new PositionCardParam(d4));

            assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof MaddeningSquareEffect));
        }

        @Test
        void should_reject_occupied_square() {
            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PositionCardParam(e1)));
        }
    }

    @Nested
    class MaddeningSquareActive {
        @BeforeEach
        void activateMaddeningSquare() {
            card.playOn(board, new PositionCardParam(d4));
            board.setTurn(Color.WHITE);
        }

        @Test
        void should_allow_rook_to_move_diagonally_from_maddening_square() {
            Rock rook = new Rock(Color.WHITE);
            board.add(rook, d4);

            // Rook on maddening square can move like a bishop (diagonally)
            assertDoesNotThrow(() -> board.tryToMove(d4, f6));
            assertEquals(rook, board.at(f6).getPiece().get());
        }

        @Test
        void should_allow_rook_to_move_normally_from_maddening_square() {
            Rock rook = new Rock(Color.WHITE);
            board.add(rook, d4);

            // Rook on maddening square can still move normally (horizontally/vertically)
            assertDoesNotThrow(() -> board.tryToMove(d4, d6));
        }

        @Test
        void should_not_allow_bishop_move_from_other_square() {
            Rock rook = new Rock(Color.WHITE);
            board.add(rook, a1);

            // Rook NOT on the maddening square cannot move diagonally
            assertThrows(Exception.class, () -> board.tryToMove(a1, c3));
        }

        @Test
        void should_allow_knight_to_move_diagonally_from_maddening_square() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, d4);

            // Knight on maddening square can move like a bishop
            assertDoesNotThrow(() -> board.tryToMove(d4, g7));
        }

        @Test
        void should_not_grant_bishop_move_through_occupied_squares() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, d4);
            // Place a piece on the diagonal path
            Pawn blocker = new Pawn(Color.WHITE);
            board.add(blocker, e5);

            // Knight tries to move diagonally through e5 to f6 from maddening square - should be blocked by blocker
            assertThrows(Exception.class, () -> board.tryToMove(d4, f6));
        }

        @Test
        void should_allow_capture_diagonally_from_maddening_square() {
            Rock rook = new Rock(Color.WHITE);
            board.add(rook, d4);
            Pawn blackPawn = new Pawn(Color.BLACK);
            board.add(blackPawn, f6);

            // Rook captures diagonally from maddening square
            assertDoesNotThrow(() -> board.tryToMove(d4, f6));
            assertTrue(board.getOutOfTheBoardPieces().contains(blackPawn));
        }
    }

    @Nested
    class PawnPromotion {
        @Test
        void should_promote_pawn_when_moving_diagonally_to_last_rank() {
            // Place maddening square on g7
            card.playOn(board, new PositionCardParam(g7));
            board.setTurn(Color.WHITE);

            Pawn whitePawn = new Pawn(Color.WHITE);
            board.add(whitePawn, g7);

            // Pawn on maddening square moves diagonally like a bishop to h8 (last rank)
            assertDoesNotThrow(() -> board.tryToMove(g7, h8));

            // Pawn should be promoted (replaced by a queen by default)
            assertTrue(board.at(h8).getPiece().get() instanceof Queen);
            assertTrue(board.drainPromotedPositions().contains(h8));
        }
    }
}
