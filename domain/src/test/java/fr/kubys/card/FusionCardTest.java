package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.FusedPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FusionCardTest {

    ChessBoard board;
    FusionCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new FusionCard();
    }

    @Test
    void should_fuse_pieces_into_fused_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);

        card.playOn(board, new PieceToPositionCardParam(knight, d4));

        assertTrue(board.at(c3).getPiece().isEmpty());
        Piece fused = board.at(d4).getPiece().orElseThrow();
        assertInstanceOf(FusedPiece.class, fused);
        assertEquals(Color.WHITE, fused.getColor());
    }

    @Test
    void fused_piece_should_move_like_either_original_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);

        card.playOn(board, new PieceToPositionCardParam(knight, d4));

        FusedPiece fused = (FusedPiece) board.at(d4).getPiece().orElseThrow();

        // Knight-like move (L-shape from d4)
        assertTrue(fused.isPositionTheoreticallyReachable(e6));
        assertTrue(fused.isPositionTheoreticallyReachable(f5));
        // Bishop-like move (diagonal from d4)
        assertTrue(fused.isPositionTheoreticallyReachable(f6));
        assertTrue(fused.isPositionTheoreticallyReachable(a1));
        // Rook-like move should fail
        assertFalse(fused.isPositionTheoreticallyReachable(d7));
        assertFalse(fused.isPositionTheoreticallyReachable(a4));
    }

    @Test
    void fused_piece_with_pawn_should_be_promotable() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, b7);
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, b8);
        // Move king out of the way to avoid check issues
        board.setTurn(Color.WHITE);

        card.playOn(board, new PieceToPositionCardParam(pawn, b8));

        // Pawn fused on rank 8 should trigger promotion
        // ChessBoard.add promotes Promotable pieces on their promotion row
        // So the fused piece gets promoted to Queen automatically
        Piece piece = board.at(b8).getPiece().orElseThrow();
        assertInstanceOf(Queen.class, piece);
    }

    @Test
    void fused_piece_without_pawn_should_not_be_promotable() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);

        card.playOn(board, new PieceToPositionCardParam(knight, d4));

        FusedPiece fused = (FusedPiece) board.at(d4).getPiece().orElseThrow();
        assertFalse(fused.isOnPromotionRow());
    }

    @Test
    void fused_piece_toString_should_show_both_pieces() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);

        card.playOn(board, new PieceToPositionCardParam(knight, d4));

        FusedPiece fused = (FusedPiece) board.at(d4).getPiece().orElseThrow();
        assertEquals("white Fused(Knight+Bishop)", fused.toString());
    }

    @Test
    void should_reject_fusing_with_enemy() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Bishop enemy = new Bishop(Color.BLACK);
        board.add(enemy, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, d4)));
    }

    @Test
    void should_reject_king_fusion() {
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(
                        board.at(e1).getPiece().get(), e1)));
    }

    @Test
    void should_reject_fusing_onto_empty_square() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, d4)));
    }
}
