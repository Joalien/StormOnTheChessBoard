package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class DerbyCardTest {

    ChessBoard board;
    DerbyCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new DerbyCard();
    }

    @Test
    void should_move_knight_again() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);
        board.move(knight, c3);

        card.playOn(board, new PieceToPositionCardParam(knight, d5));

        assertEquals(knight, board.at(d5).getPiece().get());
        assertTrue(board.at(c3).getPiece().isEmpty());
    }

    @Test
    void should_reject_if_knight_captured_a_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);
        Pawn enemyPawn = new Pawn(Color.BLACK);
        board.add(enemyPawn, c3);
        board.move(knight, c3); // capture

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, d5)));
    }

    @Test
    void should_reject_non_knight() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, c1);
        board.move(bishop, d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(bishop, e3)));
    }

    @Test
    void should_reject_if_not_last_moved_piece() {
        Knight knight1 = new Knight(Color.WHITE);
        Knight knight2 = new Knight(Color.WHITE);
        board.add(knight1, b1);
        board.add(knight2, g1);
        board.move(knight1, c3); // move knight1

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight2, f3))); // try knight2
    }
}
