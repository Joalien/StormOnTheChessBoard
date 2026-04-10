package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ExileCardTest {

    ChessBoard board;
    ExileCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ExileCard();
    }

    @Test
    void should_exile_enemy_knight_to_starting_position() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);

        card.playOn(board, new PieceToPositionCardParam(knight, b8));

        assertEquals(knight, board.at(b8).getPiece().get());
        assertTrue(board.at(d5).getPiece().isEmpty());
    }

    @Test
    void should_reject_own_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, b1)));
    }

    @Test
    void should_reject_invalid_starting_position() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, a5)));
    }

    @Test
    void should_reject_occupied_target() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);
        board.add(new Rock(Color.BLACK), b8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, b8)));
    }
}
