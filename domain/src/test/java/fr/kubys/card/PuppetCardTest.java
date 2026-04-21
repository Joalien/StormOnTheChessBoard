package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PuppetCardTest {

    ChessBoard board;
    PuppetCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        card = new PuppetCard();
    }

    @Test
    void should_swap_king_and_pawn_when_in_check() {
        King king = new King(Color.WHITE);
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(king, e1);
        board.add(new King(Color.BLACK), e8);
        board.add(pawn, a2);
        // Put king in check with a rook
        board.add(new Rock(Color.BLACK), e5);
        board.setTurn(Color.WHITE);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(king, board.at(a2).getPiece().get());
        assertEquals(pawn, board.at(e1).getPiece().get());
    }

    @Test
    void should_reject_when_king_not_in_check() {
        King king = new King(Color.WHITE);
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(king, e1);
        board.add(new King(Color.BLACK), e8);
        board.add(pawn, a2);
        board.setTurn(Color.WHITE);

        assertThrows(IllegalStateException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_non_pawn_piece() {
        King king = new King(Color.WHITE);
        Knight knight = new Knight(Color.WHITE);
        board.add(king, e1);
        board.add(new King(Color.BLACK), e8);
        board.add(knight, b1);
        board.add(new Rock(Color.BLACK), e5);
        board.setTurn(Color.WHITE);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_reject_enemy_pawn() {
        King king = new King(Color.WHITE);
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(king, e1);
        board.add(new King(Color.BLACK), e8);
        board.add(pawn, a7);
        board.add(new Rock(Color.BLACK), e5);
        board.setTurn(Color.WHITE);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_if_swap_leaves_king_in_check() {
        King king = new King(Color.WHITE);
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(king, e1);
        board.add(new King(Color.BLACK), e8);
        board.add(pawn, a2);
        // Rook checks king on e1, but also attacks a2 via rank
        board.add(new Rock(Color.BLACK), a5);
        board.setTurn(Color.WHITE);

        // After swap: king on a2, rook on a5 still checks
        assertThrows(Exception.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_swap_and_escape_check() {
        King king = new King(Color.WHITE);
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(king, e1);
        board.add(new King(Color.BLACK), e8);
        board.add(pawn, d3);
        // Rook checks on e-file
        board.add(new Rock(Color.BLACK), e5);
        board.setTurn(Color.WHITE);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(king, board.at(d3).getPiece().get());
        assertEquals(pawn, board.at(e1).getPiece().get());
    }

    @Test
    void should_promote_pawn_when_landing_on_last_rank() {
        // White king on a8, in check from black rook on h8
        King whiteKing = new King(Color.WHITE);
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whiteKing, a8);
        board.add(new King(Color.BLACK), e1);
        board.add(whitePawn, b2);
        board.add(new Rock(Color.BLACK), h8);
        board.setTurn(Color.WHITE);

        assertTrue(board.isKingUnderAttack(Color.WHITE));
        card.playOn(board, new PieceCardParam(whitePawn));

        // King moved to b2
        assertEquals(whiteKing, board.at(b2).getPiece().get());
        // Pawn landed on a8 (rank 8) and should have been promoted to Queen
        Piece promotedPiece = board.at(a8).getPiece().get();
        assertInstanceOf(Queen.class, promotedPiece);
        assertEquals(Color.WHITE, promotedPiece.getColor());
        // The promotion position should be tracked
        assertFalse(board.drainPromotedPositions().isEmpty());
    }
}
