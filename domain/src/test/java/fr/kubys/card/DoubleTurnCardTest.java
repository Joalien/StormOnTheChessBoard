package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class DoubleTurnCardTest {

    ChessBoard board;
    DoubleTurnCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new DoubleTurnCard();
    }

    @Test
    void should_move_rook_again() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        board.move(rock, a4);

        card.playOn(board, new PieceToPositionCardParam(rock, d4));

        assertEquals(rock, board.at(d4).getPiece().get());
    }

    @Test
    void should_reject_if_rook_captured() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn enemyPawn = new Pawn(Color.BLACK);
        board.add(enemyPawn, a4);
        board.move(rock, a4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, d4)));
    }

    @Test
    void should_reject_non_rook() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, c1);
        board.move(bishop, d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(bishop, e3)));
    }
}
