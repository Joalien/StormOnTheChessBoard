package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class AcrobaticCastlingCardTest {

    ChessBoard board;
    AcrobaticCastlingCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new AcrobaticCastlingCard();
    }

    @Test
    void should_castle_on_same_row() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        // King on e1, Rock on a1, same row, no pieces between
        card.playOn(board, new PieceCardParam(rock));

        // Rock moves next to King on d1 (left side), King jumps over to c1
        assertEquals(rock, board.at(d1).getPiece().orElse(null));
        assertTrue(board.at(e1).getPiece().isEmpty());
        assertTrue(board.at(c1).getPiece().filter(Piece::isKing).isPresent());
    }

    @Test
    void should_castle_on_same_row_right_side() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, h1);
        card.playOn(board, new PieceCardParam(rock));

        // Rock moves next to King on f1, King jumps over to g1
        assertEquals(rock, board.at(f1).getPiece().orElse(null));
        assertTrue(board.at(g1).getPiece().filter(Piece::isKing).isPresent());
    }

    @Test
    void should_castle_on_same_column() {
        // Move king to d4 for column test
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), d4);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);

        Rock rock = new Rock(Color.WHITE);
        board.add(rock, d1);

        card.playOn(board, new PieceCardParam(rock));

        // Rock moves next to King on d3, King jumps over to d2
        assertEquals(rock, board.at(d3).getPiece().orElse(null));
        assertTrue(board.at(d2).getPiece().filter(Piece::isKing).isPresent());
    }

    @Test
    void should_reject_non_rock_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_reject_enemy_rock() {
        Rock rock = new Rock(Color.BLACK);
        board.add(rock, a1);

        assertThrows(RuntimeException.class,
                () -> card.playOn(board, new PieceCardParam(rock)));
    }

    @Test
    void should_reject_when_not_on_same_line() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, b3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(rock)));
    }

    @Test
    void should_reject_when_pieces_between() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        board.add(new Knight(Color.WHITE), c1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(rock)));
    }

    @Test
    void should_reject_when_creates_check() {
        // Set up so that castling would expose king to check
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, h1);
        // Enemy rook on g8 attacks g1 where king would end up
        board.add(new Rock(Color.BLACK), g8);
        board.setTurn(Color.WHITE);

        assertThrows(Exception.class,
                () -> card.playOn(board, new PieceCardParam(rock)));
    }
}
