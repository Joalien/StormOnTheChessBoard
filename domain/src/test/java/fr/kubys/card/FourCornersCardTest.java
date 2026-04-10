package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BlackHoleEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FourCornersCardTest {

    ChessBoard board;
    FourCornersCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new FourCornersCard();
    }

    @Test
    void should_place_piece_in_free_corner() {
        board.add(new Bishop(Color.WHITE), a1);
        board.add(new Bishop(Color.BLACK), h1);
        board.add(new Bishop(Color.BLACK), a8);
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        card.playOn(board, new PieceCardParam(knight));

        assertEquals(knight, board.at(h8).getPiece().get());
        assertTrue(board.at(d4).getPiece().isEmpty());
    }

    @Test
    void should_promote_white_pawn_placed_in_last_rank_corner() {
        board.add(new Bishop(Color.WHITE), a1);
        board.add(new Bishop(Color.BLACK), h1);
        board.add(new Bishop(Color.BLACK), a8);
        // h8 is free — last rank for white
        WhitePawn pawn = new WhitePawn();
        board.add(pawn, d4);

        card.playOn(board, new PieceCardParam(pawn));

        // Pawn lands on h8 → auto-promoted to Queen
        assertInstanceOf(Queen.class, board.at(h8).getPiece().get());
        assertEquals(Color.WHITE, board.at(h8).getPiece().get().getColor());
    }

    @Test
    void should_reject_with_two_corners_occupied_and_black_hole_on_third() {
        // 2 corners occupied, 3rd has a black hole (no piece) → only 2 pieces on corners
        // Card requires exactly 3 occupied corners → should reject
        board.add(new Bishop(Color.WHITE), a1);
        board.add(new Bishop(Color.BLACK), h1);
        board.addEffect(new BlackHoleEffect(a8));

        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        // Black hole doesn't count as "occupied" — only 2 corners have pieces
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_reject_if_not_three_corners_occupied() {
        board.add(new Bishop(Color.WHITE), a1);
        board.add(new Bishop(Color.BLACK), h1);
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }
}
