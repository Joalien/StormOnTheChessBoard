package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CastleAndRollCardTest {

    ChessBoard board;
    CastleAndRollCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CastleAndRollCard();
    }

    @Test
    void should_validate_when_no_enemy_on_home_row() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);

        // validInput should pass, but doAction throws UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_reject_when_enemy_piece_on_home_row() {
        board.add(new Rock(Color.BLACK), a1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_reject_when_no_ally_on_home_row_except_king() {
        // Remove white king from home row, put it elsewhere
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e4);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_work_for_black_player() {
        board.setTurn(Color.BLACK);
        // Black already has king on e8 (home row), no white piece on row 8

        assertThrows(UnsupportedOperationException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_reject_when_enemy_on_black_home_row() {
        board.setTurn(Color.BLACK);
        board.add(new Rock(Color.WHITE), a8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new NoCardParam()));
    }
}
