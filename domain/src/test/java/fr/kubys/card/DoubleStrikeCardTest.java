package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.DoubleStrikeCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class DoubleStrikeCardTest {

    ChessBoard board;
    DoubleStrikeCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new DoubleStrikeCard();
    }

    @Test
    void should_move_two_knights() {
        Knight knight1 = new Knight(Color.WHITE);
        Knight knight2 = new Knight(Color.WHITE);
        board.add(knight1, b1);
        board.add(knight2, g1);

        card.playOn(board, new DoubleStrikeCardParam(knight1, c3, knight2, f3));

        assertEquals(knight1, board.at(c3).getPiece().orElse(null));
        assertEquals(knight2, board.at(f3).getPiece().orElse(null));
    }

    @Test
    void should_move_two_rooks() {
        Rock rock1 = new Rock(Color.WHITE);
        Rock rock2 = new Rock(Color.WHITE);
        board.add(rock1, a1);
        board.add(rock2, h1);

        card.playOn(board, new DoubleStrikeCardParam(rock1, a4, rock2, h4));

        assertEquals(rock1, board.at(a4).getPiece().orElse(null));
        assertEquals(rock2, board.at(h4).getPiece().orElse(null));
    }

    @Test
    void should_reject_different_piece_types() {
        Knight knight = new Knight(Color.WHITE);
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(knight, b1);
        board.add(bishop, c1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new DoubleStrikeCardParam(knight, c3, bishop, f4)));
    }

    @Test
    void should_reject_same_piece_twice() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new DoubleStrikeCardParam(knight, c3, knight, a3)));
    }

    @Test
    void should_reject_enemy_piece() {
        Knight knight1 = new Knight(Color.WHITE);
        Knight knight2 = new Knight(Color.BLACK);
        board.add(knight1, b1);
        board.add(knight2, g1);

        assertThrows(RuntimeException.class,
                () -> card.playOn(board, new DoubleStrikeCardParam(knight1, c3, knight2, f3)));
    }

    @Test
    void should_reject_king() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);
        King whiteKing = (King) board.at(e1).getPiece().get();

        // King is rejected even if paired with a same-type piece
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new DoubleStrikeCardParam(whiteKing, d1, knight, c3)));
    }

    @Test
    void should_reject_same_destination() {
        Knight knight1 = new Knight(Color.WHITE);
        Knight knight2 = new Knight(Color.WHITE);
        board.add(knight1, b1);
        board.add(knight2, d1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new DoubleStrikeCardParam(knight1, c3, knight2, c3)));
    }

    @Test
    void should_reject_invalid_move() {
        Knight knight1 = new Knight(Color.WHITE);
        Knight knight2 = new Knight(Color.WHITE);
        board.add(knight1, b1);
        board.add(knight2, g1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new DoubleStrikeCardParam(knight1, b2, knight2, f3)));
    }
}
