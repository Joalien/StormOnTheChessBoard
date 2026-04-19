package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class SnowplowCardTest {

    ChessBoard board;
    SnowplowCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new SnowplowCard();
    }

    @Test
    void should_capture_all_enemies_on_path() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn enemy1 = new Pawn(Color.BLACK);
        board.add(enemy1, a3);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(enemy2, a5);

        // Rock plows from a1 to a8 (edge of board)
        card.playOn(board, new PieceToPositionCardParam(rock, a8));

        assertEquals(rock, board.at(a8).getPiece().orElse(null));
        assertTrue(board.at(a1).getPiece().isEmpty());
        assertTrue(board.at(a3).getPiece().isEmpty());
        assertTrue(board.at(a5).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy1));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
    }

    @Test
    void should_stop_before_ally_piece() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, a3);
        Pawn ally = new Pawn(Color.WHITE);
        board.add(ally, a5);

        // Should stop at a4 (before ally at a5)
        card.playOn(board, new PieceToPositionCardParam(rock, a4));

        assertEquals(rock, board.at(a4).getPiece().orElse(null));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
        assertEquals(ally, board.at(a5).getPiece().orElse(null));
    }

    @Test
    void should_reject_knight() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, c3)));
    }

    @Test
    void should_reject_wrong_destination() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);

        // a4 is not the correct destination (should go to a8 with no ally blocking)
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a4)));
    }

    @Test
    void should_work_with_bishop() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, a1);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, c3);

        // Bishop plows diagonally to h8
        card.playOn(board, new PieceToPositionCardParam(bishop, h8));

        assertEquals(bishop, board.at(h8).getPiece().orElse(null));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_reject_enemy_piece_source() {
        Rock rock = new Rock(Color.BLACK);
        board.add(rock, a4);

        assertThrows(RuntimeException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a8)));
    }

    @Test
    void should_work_with_queen() {
        Queen queen = new Queen(Color.WHITE);
        board.add(queen, a1);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, a5);

        card.playOn(board, new PieceToPositionCardParam(queen, a8));

        assertEquals(queen, board.at(a8).getPiece().orElse(null));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }
}
