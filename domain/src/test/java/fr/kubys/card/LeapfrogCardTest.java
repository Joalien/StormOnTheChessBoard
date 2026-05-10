package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.LeapfrogCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class LeapfrogCardTest {

    ChessBoard board;
    LeapfrogCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new LeapfrogCard();
    }

    @Test
    void should_jump_over_one_piece_and_capture_it() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight jumped = new Knight(Color.WHITE);
        board.add(pawn, d2);
        board.add(jumped, e3);

        card.playOn(board, new LeapfrogCardParam(pawn, List.of(f4)));

        assertEquals(pawn, board.at(f4).getPiece().get());
        assertTrue(board.at(d2).getPiece().isEmpty());
        assertTrue(board.at(e3).getPiece().isEmpty(), "Jumped piece should be removed from the board");
        assertTrue(board.getOutOfTheBoardPieces().contains(jumped), "Jumped piece should be among captured pieces");
    }

    @Test
    void should_capture_jumped_enemy_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d2);
        board.add(enemy, e3);

        card.playOn(board, new LeapfrogCardParam(pawn, List.of(f4)));

        assertEquals(pawn, board.at(f4).getPiece().get());
        assertTrue(board.at(e3).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_capture_every_piece_along_a_multi_jump() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight first = new Knight(Color.WHITE);
        Knight second = new Knight(Color.BLACK);
        board.add(pawn, d2);
        board.add(first, e3);
        board.add(second, g5);

        card.playOn(board, new LeapfrogCardParam(pawn, List.of(f4, h6)));

        assertEquals(pawn, board.at(h6).getPiece().get());
        assertTrue(board.at(d2).getPiece().isEmpty());
        assertTrue(board.at(e3).getPiece().isEmpty());
        assertTrue(board.at(g5).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(first));
        assertTrue(board.getOutOfTheBoardPieces().contains(second));
    }

    @Test
    void should_reject_if_no_piece_to_jump_over() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new LeapfrogCardParam(pawn, List.of(f4))));
    }

    @Test
    void should_reject_if_landing_occupied() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);
        board.add(new Knight(Color.WHITE), e3);
        board.add(new Rock(Color.WHITE), f4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new LeapfrogCardParam(pawn, List.of(f4))));
    }

    @Test
    void should_reject_non_pawn() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d2);
        board.add(new Knight(Color.WHITE), e3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new LeapfrogCardParam(rook, List.of(f4))));
    }

    @Test
    void should_reject_enemy_pawn() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d7);
        board.add(new Knight(Color.BLACK), e6);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new LeapfrogCardParam(pawn, List.of(f5))));
    }

    @Test
    void should_allow_change_direction_during_multi_jump() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight ally = new Knight(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d2);
        board.add(ally, e3);
        board.add(enemy, e5);

        card.playOn(board, new LeapfrogCardParam(pawn, List.of(f4, d6)));

        assertEquals(pawn, board.at(d6).getPiece().get());
        assertTrue(board.at(e3).getPiece().isEmpty());
        assertTrue(board.at(e5).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(ally));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_reject_non_diagonal_jump() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);
        board.add(new Knight(Color.WHITE), e3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new LeapfrogCardParam(pawn, List.of(d3))));
    }

    @Test
    void should_reject_empty_positions_list() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new LeapfrogCardParam(pawn, List.of())));
    }

    @Test
    void should_promote_pawn_on_last_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d6);
        board.add(new Knight(Color.BLACK), e7);

        card.playOn(board, new LeapfrogCardParam(pawn, List.of(f8)));

        Piece pieceOnF8 = board.at(f8).getPiece().get();
        assertInstanceOf(Queen.class, pieceOnF8);
        assertEquals(Color.WHITE, pieceOnF8.getColor());
    }

    @Test
    void should_promote_black_pawn_on_first_rank() {
        board.setTurn(Color.BLACK);
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, e3);
        board.add(new Knight(Color.WHITE), d2);

        card.playOn(board, new LeapfrogCardParam(pawn, List.of(c1)));

        Piece pieceOnC1 = board.at(c1).getPiece().get();
        assertInstanceOf(Queen.class, pieceOnC1);
        assertEquals(Color.BLACK, pieceOnC1.getColor());
    }

    @Test
    void should_validate_each_jump_in_sequence() {
        // Second jump has no piece to jump over
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);
        board.add(new Knight(Color.WHITE), e3);
        // No piece on g5 to jump from f4 to h6

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new LeapfrogCardParam(pawn, List.of(f4, h6))));
    }
}
