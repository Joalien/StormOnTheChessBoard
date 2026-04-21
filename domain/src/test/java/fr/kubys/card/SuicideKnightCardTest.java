package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class SuicideKnightCardTest {

    ChessBoard board;
    SuicideKnightCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        card = new SuicideKnightCard();
    }

    @Test
    void should_capture_all_reachable_enemy_pieces_and_remove_knight() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        // Knight on d4 can attack: b3, b5, c2, c6, e2, e6, f3, f5
        Pawn enemy1 = new Pawn(Color.BLACK);
        board.add(enemy1, b3);
        Rock enemy2 = new Rock(Color.BLACK);
        board.add(enemy2, f5);

        card.playOn(board, new KnightCardParam(knight));

        assertTrue(board.getOutOfTheBoardPieces().contains(enemy1));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertTrue(board.at(d4).getPiece().isEmpty());
        assertTrue(board.at(b3).getPiece().isEmpty());
        assertTrue(board.at(f5).getPiece().isEmpty());
    }

    @Test
    void should_not_capture_enemy_king() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, g6);
        // Knight on g6 can attack h8 (black king), but should not capture it
        Rock enemy1 = new Rock(Color.BLACK);
        board.add(enemy1, f4);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(enemy2, e5);

        card.playOn(board, new KnightCardParam(knight));

        assertTrue(board.at(h8).getPiece().isPresent(), "Black king should survive");
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy1));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
    }

    @Test
    void should_reject_if_only_one_capturable() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, b3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new KnightCardParam(knight)));
    }

    @Test
    void should_reject_if_no_capturable() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new KnightCardParam(knight)));
    }

    @Test
    void should_reject_enemy_knight() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d4);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new KnightCardParam(knight)));
    }

    @Test
    void should_not_capture_own_pieces() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Bishop ally = new Bishop(Color.WHITE);
        board.add(ally, b3);
        Rock enemy1 = new Rock(Color.BLACK);
        board.add(enemy1, f5);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(enemy2, e6);

        card.playOn(board, new KnightCardParam(knight));

        assertFalse(board.getOutOfTheBoardPieces().contains(ally));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy1));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
    }

    @Test
    void should_reject_if_explosion_creates_discovered_check() {
        // White knight on e4 blocks rank 4 between black rook a4 and white king h4.
        // Removing the knight via explosion opens the rank for the black rook.
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), h4);
        board.add(new King(Color.BLACK), a8);
        board.setTurn(Color.WHITE);

        Knight knight = new Knight(Color.WHITE);
        board.add(knight, e4);
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyRook, a4);
        Pawn enemy1 = new Pawn(Color.BLACK);
        board.add(enemy1, d6);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(enemy2, f6);

        assertThrows(CheckException.class,
                () -> card.playOn(board, new KnightCardParam(knight)));
    }
}
