package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class RelayCardTest {

    ChessBoard board;
    RelayCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new RelayCard();
    }

    @Test
    void should_pass_through_ally_piece() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn ally = new Pawn(Color.WHITE);
        board.add(ally, a4);

        // Rock passes through ally at a4 to reach a7
        card.playOn(board, new PieceToPositionCardParam(rock, a7));

        assertEquals(rock, board.at(a7).getPiece().orElse(null));
        assertTrue(board.at(a1).getPiece().isEmpty());
        assertEquals(ally, board.at(a4).getPiece().orElse(null)); // ally not captured
    }

    @Test
    void should_reject_when_enemy_on_path() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, a4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a7)));
    }

    @Test
    void should_reject_capture() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn ally = new Pawn(Color.WHITE);
        board.add(ally, a4);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, a7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a7)));
    }

    @Test
    void should_reject_when_no_ally_on_path() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);

        // No ally on path - this is just a normal move, not a relay
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a7)));
    }

    @Test
    void should_work_with_bishop() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, a1);
        Pawn ally = new Pawn(Color.WHITE);
        board.add(ally, c3);

        card.playOn(board, new PieceToPositionCardParam(bishop, e5));

        assertEquals(bishop, board.at(e5).getPiece().orElse(null));
        assertEquals(ally, board.at(c3).getPiece().orElse(null));
    }

    @Test
    void should_reject_knight() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, c3)));
    }

    @Test
    void should_reject_enemy_piece() {
        Rock rock = new Rock(Color.BLACK);
        board.add(rock, a4);

        assertThrows(RuntimeException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a7)));
    }

    @Test
    void should_pass_through_multiple_allies() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        board.add(new Pawn(Color.WHITE), a3);
        board.add(new Pawn(Color.WHITE), a5);

        card.playOn(board, new PieceToPositionCardParam(rock, a7));

        assertEquals(rock, board.at(a7).getPiece().orElse(null));
    }
}
