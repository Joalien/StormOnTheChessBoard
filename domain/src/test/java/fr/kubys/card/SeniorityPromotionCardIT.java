package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class SeniorityPromotionCardIT {

    @Test
    void should_promote_two_pawns_to_different_pieces() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new WhitePawn(), c4);
        board.add(new WhitePawn(), f5);
        board.setTurn(Color.WHITE);

        new SeniorityPromotionCard().playOn(board, new NoCardParam());

        // Both promoted to Rock by default
        assertInstanceOf(Rock.class, board.at(c4).getPiece().get());
        assertInstanceOf(Rock.class, board.at(f5).getPiece().get());

        // Player overrides: c4 → Bishop, f5 → Knight
        board.overridePromotion(c4, PromotionPiece.BISHOP);
        board.overridePromotion(f5, PromotionPiece.KNIGHT);

        assertInstanceOf(Bishop.class, board.at(c4).getPiece().get());
        assertEquals(Color.WHITE, board.at(c4).getPiece().get().getColor());
        assertInstanceOf(Knight.class, board.at(f5).getPiece().get());
        assertEquals(Color.WHITE, board.at(f5).getPiece().get().getColor());
    }

    @Test
    void should_promote_single_pawn_and_override_to_bishop() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new WhitePawn(), d4);
        board.setTurn(Color.WHITE);

        new SeniorityPromotionCard().playOn(board, new NoCardParam());
        board.overridePromotion(d4, PromotionPiece.BISHOP);

        assertInstanceOf(Bishop.class, board.at(d4).getPiece().get());
    }

    @Test
    void should_promote_black_pawns_and_override() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new BlackPawn(), a5);
        board.add(new BlackPawn(), h6);
        board.setTurn(Color.BLACK);

        new SeniorityPromotionCard().playOn(board, new NoCardParam());

        // Override one to Knight, keep the other as Rock
        board.overridePromotion(a5, PromotionPiece.KNIGHT);

        assertInstanceOf(Knight.class, board.at(a5).getPiece().get());
        assertEquals(Color.BLACK, board.at(a5).getPiece().get().getColor());
        assertInstanceOf(Rock.class, board.at(h6).getPiece().get());
        assertEquals(Color.BLACK, board.at(h6).getPiece().get().getColor());
    }

    @Test
    void should_keep_promoted_pieces_functional_on_board() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new WhitePawn(), d4);
        board.setTurn(Color.WHITE);

        new SeniorityPromotionCard().playOn(board, new NoCardParam());
        board.overridePromotion(d4, PromotionPiece.KNIGHT);

        // The new Knight should be functional — can move in L-shape
        Piece knight = board.at(d4).getPiece().get();
        assertDoesNotThrow(() -> board.tryToMove(d4, c6));
        assertEquals(knight, board.at(c6).getPiece().get());
    }
}
