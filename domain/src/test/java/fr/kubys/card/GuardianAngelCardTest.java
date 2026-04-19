package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class GuardianAngelCardTest {

    ChessBoard board;
    GuardianAngelCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new GuardianAngelCard();
    }

    @Test
    void should_intercept_check_with_captured_piece() {
        // Put white king in check from black rook
        Rock blackRook = new Rock(Color.BLACK);
        board.add(blackRook, e5);
        // Capture a white piece first so it's available
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, d4);
        board.removePieceFromTheBoard(whiteKnight, fr.kubys.board.PieceRemoval.RemovalReason.CAPTURED);

        // Now white king is in check from e5 rook
        assertTrue(board.isKingUnderAttack(Color.WHITE));

        // Place captured knight between king (e1) and rook (e5)
        card.playOn(board, new PieceToPositionCardParam(whiteKnight, e3));

        assertTrue(board.at(e3).getPiece().isPresent());
        assertEquals(whiteKnight, board.at(e3).getPiece().get());
    }

    @Test
    void should_reject_when_not_in_check() {
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, d4);
        board.removePieceFromTheBoard(whiteKnight, fr.kubys.board.PieceRemoval.RemovalReason.CAPTURED);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(whiteKnight, e3)));
    }

    @Test
    void should_reject_piece_not_captured() {
        Rock blackRook = new Rock(Color.BLACK);
        board.add(blackRook, e5);
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, d4); // still on board

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(whiteKnight, e3)));
    }
}
