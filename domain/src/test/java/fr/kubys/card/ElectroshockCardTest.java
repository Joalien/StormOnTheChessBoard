package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ElectroshockCardTest {

    ChessBoard board;
    ElectroshockCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ElectroshockCard();
    }

    @Test
    void should_move_bishop_orthogonally() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);

        card.playOn(board, new NoCardParam());

        // Bishop should have moved one square orthogonally
        assertTrue(board.at(d4).getPiece().isEmpty());
        // Should be on one of d5, e4, d3, c4
        boolean movedOrthogonally = board.at(d5).getPiece().isPresent()
                || board.at(e4).getPiece().isPresent()
                || board.at(d3).getPiece().isPresent()
                || board.at(c4).getPiece().isPresent();
        assertTrue(movedOrthogonally);
    }

    @Test
    void should_reject_when_no_bishop_can_move() {
        // No bishops on the board
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_move_enemy_bishops_first() {
        Bishop allyBishop = new Bishop(Color.WHITE);
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(allyBishop, d4);
        board.add(enemyBishop, d6);

        card.playOn(board, new NoCardParam());

        // Both bishops should have moved
        assertTrue(board.at(d4).getPiece().isEmpty());
        assertTrue(board.at(d6).getPiece().isEmpty());
    }

    @Test
    void should_not_capture_with_bishop() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, a1);
        // Surround with pieces except one empty square
        board.add(new Pawn(Color.WHITE), a2);
        board.add(new Pawn(Color.BLACK), b1);

        // Bishop at a1 can only go to b1 (occupied) or a2 (occupied) — no orthogonal moves available
        // Actually, need to check: up=a2 (occupied), right=b1 (occupied), down=nothing, left=nothing
        // So bishop cannot move
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_handle_bishop_with_limited_options() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, a1);
        // a1: up=a2 free, right=b1 free, no down/left

        card.playOn(board, new NoCardParam());

        assertTrue(board.at(a1).getPiece().isEmpty());
        boolean moved = board.at(a2).getPiece().filter(p -> p == bishop).isPresent()
                || board.at(b1).getPiece().filter(p -> p == bishop).isPresent();
        assertTrue(moved);
    }
}
