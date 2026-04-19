package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PsychopathCardTest {

    ChessBoard board;
    PsychopathCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        card = new PsychopathCard();
    }

    @Test
    void should_capture_all_reachable_enemy_pieces_and_remove_bishop() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);
        Knight enemy1 = new Knight(Color.BLACK);
        board.add(enemy1, f6);
        Rock enemy2 = new Rock(Color.BLACK);
        board.add(enemy2, b2);

        card.playOn(board, new PieceCardParam(bishop));

        assertTrue(board.getOutOfTheBoardPieces().contains(enemy1));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
        assertTrue(board.getOutOfTheBoardPieces().contains(bishop));
        assertTrue(board.at(d4).getPiece().isEmpty());
        assertTrue(board.at(f6).getPiece().isEmpty());
        assertTrue(board.at(b2).getPiece().isEmpty());
    }

    @Test
    void should_not_capture_enemy_king() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);
        // h8 has black king on the same diagonal
        Rock enemy = new Rock(Color.BLACK);
        board.add(enemy, b2);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(enemy2, f6);

        card.playOn(board, new PieceCardParam(bishop));

        assertTrue(board.at(h8).getPiece().isPresent(), "Black king should survive");
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
        assertTrue(board.getOutOfTheBoardPieces().contains(bishop));
    }

    @Test
    void should_reject_if_only_one_capturable() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);
        Knight enemy = new Knight(Color.BLACK);
        board.add(enemy, f6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(bishop)));
    }

    @Test
    void should_reject_if_no_capturable() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(bishop)));
    }

    @Test
    void should_reject_non_bishop() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_reject_enemy_bishop() {
        Bishop bishop = new Bishop(Color.BLACK);
        board.add(bishop, d4);
        Rock enemy1 = new Rock(Color.WHITE);
        board.add(enemy1, f6);
        Rock enemy2 = new Rock(Color.WHITE);
        board.add(enemy2, b2);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new PieceCardParam(bishop)));
    }

    @Test
    void should_not_capture_own_pieces() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, d4);
        Knight ally = new Knight(Color.WHITE);
        board.add(ally, c3);
        Rock enemy1 = new Rock(Color.BLACK);
        board.add(enemy1, f6);
        Rock enemy2 = new Rock(Color.BLACK);
        board.add(enemy2, e3);

        card.playOn(board, new PieceCardParam(bishop));

        assertFalse(board.getOutOfTheBoardPieces().contains(ally));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy1));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
        assertTrue(board.getOutOfTheBoardPieces().contains(bishop));
    }

    @Test
    void should_reject_if_explosion_creates_discovered_check() {
        // White bishop on f3 blocks diagonal a8-h1 protecting white king on h1.
        // Removing the bishop via explosion opens the diagonal for black bishop on a8.
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), h1);
        board.add(new King(Color.BLACK), a7);
        board.setTurn(Color.WHITE);

        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, f3);
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, a8);
        Pawn enemy1 = new Pawn(Color.BLACK);
        board.add(enemy1, e4);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(enemy2, g4);

        assertThrows(CheckException.class,
                () -> card.playOn(board, new PieceCardParam(bishop)));
    }
}
