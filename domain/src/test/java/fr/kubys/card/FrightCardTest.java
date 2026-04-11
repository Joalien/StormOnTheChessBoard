package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FrightCardTest {

    ChessBoard board;
    FrightCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new FrightCard();
    }

    @Test
    void should_push_back_black_pawn_one_square() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d5);

        card.playOn(board, new PieceToPositionCardParam(pawn, d6));

        assertEquals(pawn, board.at(d6).getPiece().get());
        assertTrue(board.at(d5).getPiece().isEmpty());
    }

    @Test
    void should_push_back_black_pawn_two_squares() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d5);

        card.playOn(board, new PieceToPositionCardParam(pawn, d7));

        assertEquals(pawn, board.at(d7).getPiece().get());
    }

    @Test
    void should_push_back_black_pawn_to_first_rank_and_not_promote() {
        // A black pawn pushed back to row 8 (its starting side) should NOT promote
        // because promotion row for black pawn is Row.One, not Row.Eight
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, a7);

        card.playOn(board, new PieceToPositionCardParam(pawn, a8));

        assertInstanceOf(Pawn.class, board.at(a8).getPiece().get());
    }

    @Test
    void should_push_back_white_pawn_to_first_rank() {
        // Push a white pawn back to row 1 (its starting side)
        // White pawn promotion is Row.Eight, so row 1 should NOT promote
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a3);
        board.setTurn(Color.BLACK);

        card.playOn(board, new PieceToPositionCardParam(pawn, a1));

        assertInstanceOf(Pawn.class, board.at(a1).getPiece().get());
    }

    @Test
    void should_reject_pushing_forward() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d4)));
    }

    @Test
    void should_reject_own_pawn() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d3)));
    }

    @Test
    void should_reject_occupied_target() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d5);
        board.add(new Knight(Color.BLACK), d6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d6)));
    }

    @Test
    void should_reject_three_squares() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d7)));
    }
}
