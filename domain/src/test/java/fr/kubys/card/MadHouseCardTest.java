package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.MadHouseCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MadHouseCardTest {

    ChessBoard board;
    MadHouseCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        card = new MadHouseCard();
    }

    @Test
    void should_swap_rock_and_bishop() {
        Rock rock = new Rock(Color.WHITE);
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(rock, a1);
        board.add(bishop, c1);

        card.playOn(board, new MadHouseCardParam(rock, bishop));

        assertEquals(rock, board.at(c1).getPiece().get());
        assertEquals(bishop, board.at(a1).getPiece().get());
    }

    @Test
    void should_swap_black_pieces() {
        board.setTurn(Color.BLACK);
        Rock rock = new Rock(Color.BLACK);
        Bishop bishop = new Bishop(Color.BLACK);
        board.add(rock, a8);
        board.add(bishop, f8);

        card.playOn(board, new MadHouseCardParam(rock, bishop));

        assertEquals(rock, board.at(f8).getPiece().get());
        assertEquals(bishop, board.at(a8).getPiece().get());
    }

    @Test
    void should_reject_different_colors() {
        Rock rock = new Rock(Color.WHITE);
        Bishop bishop = new Bishop(Color.BLACK);
        board.add(rock, a1);
        board.add(bishop, c8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new MadHouseCardParam(rock, bishop)));
    }

    @Test
    void should_reject_swapping_opponent_pieces() {
        board.setTurn(Color.WHITE);
        Rock rock = new Rock(Color.BLACK);
        Bishop bishop = new Bishop(Color.BLACK);
        board.add(rock, a8);
        board.add(bishop, f8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new MadHouseCardParam(rock, bishop)));
    }
}
