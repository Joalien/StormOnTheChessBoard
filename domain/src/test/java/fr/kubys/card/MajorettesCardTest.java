package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.MajorettesCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class MajorettesCardTest {

    ChessBoard board;
    MajorettesCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new MajorettesCard();
    }

    @Test
    void should_move_pawn_left() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, d4);
        board.add(p2, f4);

        card.playOn(board, new MajorettesCardParam(p1, MajorettesCard.Direction.LEFT, p2, MajorettesCard.Direction.LEFT));

        assertTrue(board.at(c4).getPiece().isPresent());
        assertTrue(board.at(e4).getPiece().isPresent());
        assertTrue(board.at(d4).getPiece().isEmpty());
        assertTrue(board.at(f4).getPiece().isEmpty());
    }

    @Test
    void should_move_pawn_right() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, b4);
        board.add(p2, f4);

        card.playOn(board, new MajorettesCardParam(p1, MajorettesCard.Direction.RIGHT, p2, MajorettesCard.Direction.RIGHT));

        assertTrue(board.at(c4).getPiece().isPresent());
        assertTrue(board.at(g4).getPiece().isPresent());
    }

    @Test
    void should_move_two_pawns_in_different_directions() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, c4);
        board.add(p2, f4);

        card.playOn(board, new MajorettesCardParam(p1, MajorettesCard.Direction.RIGHT, p2, MajorettesCard.Direction.LEFT));

        assertTrue(board.at(d4).getPiece().isPresent());
        assertTrue(board.at(e4).getPiece().isPresent());
    }

    @Test
    void should_reject_when_direction_blocked_by_edge() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, a4);
        board.add(p2, f4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new MajorettesCardParam(p1, MajorettesCard.Direction.LEFT, p2, MajorettesCard.Direction.RIGHT)));
    }

    @Test
    void should_reject_when_direction_blocked_by_piece() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        Pawn blocker = new Pawn(Color.WHITE);
        board.add(p1, c4);
        board.add(p2, f4);
        board.add(blocker, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new MajorettesCardParam(p1, MajorettesCard.Direction.RIGHT, p2, MajorettesCard.Direction.LEFT)));
    }

    @Test
    void should_reject_null_direction() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, c4);
        board.add(p2, f4);

        assertThrows(IllegalStateException.class,
                () -> card.playOn(board, new MajorettesCardParam(p1, null, p2, MajorettesCard.Direction.LEFT)));
    }

    @Test
    void should_reject_non_pawns() {
        Knight k = new Knight(Color.WHITE);
        Pawn p = new Pawn(Color.WHITE);
        board.add(k, b1);
        board.add(p, c4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new MajorettesCardParam(k, MajorettesCard.Direction.LEFT, p, MajorettesCard.Direction.RIGHT)));
    }

    @Test
    void should_reject_same_pawn_twice() {
        Pawn p1 = new Pawn(Color.WHITE);
        board.add(p1, c4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new MajorettesCardParam(p1, MajorettesCard.Direction.LEFT, p1, MajorettesCard.Direction.RIGHT)));
    }

    @Test
    void should_reject_opponent_pawns() {
        Pawn p1 = new Pawn(Color.BLACK);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, c4);
        board.add(p2, f4);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new MajorettesCardParam(p1, MajorettesCard.Direction.LEFT, p2, MajorettesCard.Direction.RIGHT)));
    }
}
