package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class TrailerCardTest {

    ChessBoard board;
    TrailerCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new TrailerCard();
    }

    @Test
    void should_move_pawn_and_pull_piece_behind() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d3);
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d2);

        card.playOn(board, new PieceToPositionCardParam(pawn, d4));

        assertEquals(pawn, board.at(d4).getPiece().get());
        assertEquals(rook, board.at(d3).getPiece().get());
        assertTrue(board.at(d2).getPiece().isEmpty());
    }

    @Test
    void should_pull_enemy_piece_behind() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a3);
        Rock enemy = new Rock(Color.BLACK);
        board.add(enemy, a2);

        card.playOn(board, new PieceToPositionCardParam(pawn, a4));

        assertEquals(pawn, board.at(a4).getPiece().get());
        assertEquals(enemy, board.at(a3).getPiece().get());
    }

    @Test
    void should_reject_non_pawn() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d3);
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rook, d4)));
    }

    @Test
    void should_reject_if_destination_not_forward() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d3);
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d2);

        // Try to move sideways
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, e3)));
    }

    @Test
    void should_allow_two_squares_forward() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d1);

        card.playOn(board, new PieceToPositionCardParam(pawn, d4));

        assertEquals(pawn, board.at(d4).getPiece().get());
        assertEquals(rook, board.at(d3).getPiece().get());
    }

    @Test
    void should_reject_if_destination_occupied() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d3);
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d2);
        Knight blocker = new Knight(Color.BLACK);
        board.add(blocker, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d4)));
    }

    @Test
    void should_reject_if_no_piece_behind() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d4)));
    }

    @Test
    void should_reject_enemy_pawn() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d6);
        Rock rook = new Rock(Color.BLACK);
        board.add(rook, d7);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d5)));
    }

    @Test
    void should_work_for_black_pawn() {
        board.setTurn(Color.BLACK);
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d6);
        Rock rook = new Rock(Color.BLACK);
        board.add(rook, d7);

        card.playOn(board, new PieceToPositionCardParam(pawn, d5));

        assertEquals(pawn, board.at(d5).getPiece().get());
        assertEquals(rook, board.at(d6).getPiece().get());
        assertTrue(board.at(d7).getPiece().isEmpty());
    }
}
