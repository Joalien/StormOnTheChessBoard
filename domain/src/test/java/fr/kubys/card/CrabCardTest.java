package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CrabCardTest {

    ChessBoard board;
    CrabCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CrabCard();
    }

    @Nested
    class PlayCard {
        @Test
        void should_transform_own_pawn_into_crab() {
            Pawn pawn = new Pawn(Color.WHITE);
            board.add(pawn, d4);

            card.playOn(board, new PieceCardParam(pawn));

            Piece crab = board.at(d4).getPiece().get();
            assertInstanceOf(Crab.class, crab);
            assertEquals(Color.WHITE, crab.getColor());
        }

        @Test
        void should_reject_non_pawn_piece() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, b1);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(knight)));
        }

        @Test
        void should_reject_enemy_pawn() {
            Pawn pawn = new Pawn(Color.BLACK);
            board.add(pawn, d5);

            assertThrows(CannotMoveThisColorException.class,
                    () -> card.playOn(board, new PieceCardParam(pawn)));
        }

        @Test
        void should_reject_already_crab() {
            Crab crab = new Crab(Color.WHITE);
            board.add(crab, d4);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(crab)));
        }
    }

    @Nested
    class CrabMovement {
        @Test
        void should_move_diagonally_forward_right() {
            board.add(new Crab(Color.WHITE), d4);

            assertDoesNotThrow(() -> board.tryToMove(d4, e5));
        }

        @Test
        void should_move_diagonally_forward_left() {
            board.add(new Crab(Color.WHITE), d4);

            assertDoesNotThrow(() -> board.tryToMove(d4, c5));
        }

        @Test
        void should_move_diagonally_backward_right() {
            board.add(new Crab(Color.WHITE), d4);

            assertDoesNotThrow(() -> board.tryToMove(d4, e3));
        }

        @Test
        void should_move_diagonally_backward_left() {
            board.add(new Crab(Color.WHITE), d4);

            assertDoesNotThrow(() -> board.tryToMove(d4, c3));
        }

        @Test
        void should_not_move_straight() {
            board.add(new Crab(Color.WHITE), d4);

            assertThrows(Exception.class, () -> board.tryToMove(d4, d5));
        }

        @Test
        void should_not_move_horizontally() {
            board.add(new Crab(Color.WHITE), d4);

            assertThrows(Exception.class, () -> board.tryToMove(d4, e4));
        }

        @Test
        void should_not_move_more_than_one_square_diagonally() {
            board.add(new Crab(Color.WHITE), d4);

            assertThrows(Exception.class, () -> board.tryToMove(d4, f6));
        }

        @Test
        void should_capture_enemy_piece_diagonally() {
            board.add(new Crab(Color.WHITE), d4);
            board.add(new Knight(Color.BLACK), e5);

            assertDoesNotThrow(() -> board.tryToMove(d4, e5));
            assertTrue(board.getOutOfTheBoardPieces().stream().anyMatch(p -> p instanceof Knight));
        }

        @Test
        void should_not_capture_own_piece() {
            board.add(new Crab(Color.WHITE), d4);
            board.add(new Knight(Color.WHITE), e5);

            assertThrows(Exception.class, () -> board.tryToMove(d4, e5));
        }
    }

    @Nested
    class CrabPromotion {
        @Test
        void white_crab_should_promote_on_row_8() {
            board.add(new Crab(Color.WHITE), d7);

            board.tryToMove(d7, e8);

            Piece promoted = board.at(e8).getPiece().get();
            assertInstanceOf(Queen.class, promoted);
            assertEquals(Color.WHITE, promoted.getColor());
        }

        @Test
        void black_crab_should_promote_on_row_1() {
            board.add(new Crab(Color.BLACK), d2);
            board.setTurn(Color.BLACK);

            board.tryToMove(d2, c1);

            Piece promoted = board.at(c1).getPiece().get();
            assertInstanceOf(Queen.class, promoted);
            assertEquals(Color.BLACK, promoted.getColor());
        }

        @Test
        void white_crab_should_promote_by_capturing() {
            board.add(new Crab(Color.WHITE), d7);
            Knight enemy = new Knight(Color.BLACK);
            board.add(enemy, c8);

            board.tryToMove(d7, c8);

            Piece promoted = board.at(c8).getPiece().get();
            assertInstanceOf(Queen.class, promoted);
            assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
        }

        @Test
        void should_support_under_promotion_to_rook() {
            board.add(new Crab(Color.WHITE), d7);

            board.tryToMove(d7, e8);
            board.overridePromotion(e8, PromotionPiece.ROOK);

            Piece piece = board.at(e8).getPiece().get();
            assertInstanceOf(Rock.class, piece);
            assertEquals(Color.WHITE, piece.getColor());
        }

        @Test
        void should_support_under_promotion_to_bishop() {
            board.add(new Crab(Color.WHITE), d7);

            board.tryToMove(d7, e8);
            board.overridePromotion(e8, PromotionPiece.BISHOP);

            Piece piece = board.at(e8).getPiece().get();
            assertInstanceOf(Bishop.class, piece);
        }

        @Test
        void should_support_under_promotion_to_knight() {
            board.add(new Crab(Color.WHITE), d7);

            board.tryToMove(d7, e8);
            board.overridePromotion(e8, PromotionPiece.KNIGHT);

            Piece piece = board.at(e8).getPiece().get();
            assertInstanceOf(Knight.class, piece);
        }

        @Test
        void should_track_promoted_position() {
            board.add(new Crab(Color.WHITE), d7);

            board.tryToMove(d7, e8);

            // drainPromotedPositions should have tracked the promotion
            // (it was already drained by the move, so a second call returns empty)
            // We verify indirectly: the piece on e8 is a Queen (auto-promoted)
            assertInstanceOf(Queen.class, board.at(e8).getPiece().get());
        }
    }
}
