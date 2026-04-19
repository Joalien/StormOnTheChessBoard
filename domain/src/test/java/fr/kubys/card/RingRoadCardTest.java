package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.board.CheckException;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class RingRoadCardTest {

    ChessBoard board;
    RingRoadCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new RingRoadCard();
    }

    @Test
    void should_move_piece_from_border_to_border() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a4); // border (file A)

        card.playOn(board, new PieceToPositionCardParam(rock, h6)); // border (file H)

        assertTrue(board.at(a4).getPiece().isEmpty());
        assertTrue(board.at(h6).getPiece().isPresent());
        assertEquals(rock, board.at(h6).getPiece().get());
    }

    @Test
    void should_reject_piece_not_on_border() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4); // not on border

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, a5)));
    }

    @Test
    void should_reject_destination_not_on_border() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a4); // on border

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, d4))); // not border
    }

    @Test
    void should_reject_pawn() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, a6)));
    }

    @Test
    void should_reject_occupied_destination() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a4);
        Pawn blocker = new Pawn(Color.WHITE);
        board.add(blocker, h4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, h4)));
    }

    @Test
    void should_reject_enemy_piece() {
        Rock enemyRock = new Rock(Color.BLACK);
        board.add(enemyRock, a4);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyRock, h4)));
    }

    @Test
    void should_reject_if_creates_check() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a2);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        Rock whiteRock = new Rock(Color.WHITE);
        board.add(whiteRock, a4);
        Rock blackRook = new Rock(Color.BLACK);
        board.add(blackRook, a8);

        // Moving white rook from a4 to h4 would expose king on a2 to black rook on a8
        assertThrows(CheckException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(whiteRock, h4)));
    }
}
