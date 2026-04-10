package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class SeniorityPromotionCardTest {

    ChessBoard board;
    SeniorityPromotionCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new SeniorityPromotionCard();
    }

    @Test
    void should_promote_last_pawn() {
        board.add(new Pawn(Color.WHITE), d4);

        card.playOn(board, new NoCardParam());

        assertInstanceOf(Rock.class, board.at(d4).getPiece().get());
    }

    @Test
    void should_promote_both_pawns_when_two_remain() {
        board.add(new Pawn(Color.WHITE), d4);
        board.add(new Pawn(Color.WHITE), f4);

        card.playOn(board, new NoCardParam());

        assertInstanceOf(Rock.class, board.at(d4).getPiece().get());
        assertInstanceOf(Rock.class, board.at(f4).getPiece().get());
    }

    @Test
    void should_allow_under_promotion_to_bishop() {
        board.add(new Pawn(Color.WHITE), d4);

        card.playOn(board, new NoCardParam());
        board.overridePromotion(d4, PromotionPiece.BISHOP);

        assertInstanceOf(Bishop.class, board.at(d4).getPiece().get());
        assertEquals(Color.WHITE, board.at(d4).getPiece().get().getColor());
    }

    @Test
    void should_allow_under_promotion_to_knight() {
        board.add(new Pawn(Color.WHITE), d4);

        card.playOn(board, new NoCardParam());
        board.overridePromotion(d4, PromotionPiece.KNIGHT);

        assertInstanceOf(Knight.class, board.at(d4).getPiece().get());
    }

    @Test
    void should_not_promote_to_queen() {
        board.add(new Pawn(Color.WHITE), d4);

        card.playOn(board, new NoCardParam());

        // Default promotion is Rook, NOT Queen (Queen is excluded by card rules)
        assertInstanceOf(Rock.class, board.at(d4).getPiece().get());
    }

    @Test
    void should_reject_with_three_pawns() {
        board.add(new Pawn(Color.WHITE), d4);
        board.add(new Pawn(Color.WHITE), f4);
        board.add(new Pawn(Color.WHITE), h4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_reject_with_no_pawns() {
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new NoCardParam()));
    }
}
