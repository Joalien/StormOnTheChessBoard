package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CylinderCardTest {

    ChessBoard board;
    CylinderCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CylinderCard();
    }

    @Test
    void should_move_rook_wrapping_left_to_right() {
        // Rook on a4, wrapping to h4 (shorter path going left: a->h)
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, b4);

        // Wrap: b4 -> a4 (direct) is shorter, but h4 via wrapping would be b->a->h
        // Actually b4 to h4 direct is 6 squares, wrapping is 2 squares (b->a->h)
        card.playOn(board, new PieceToPositionCardParam(rock, h4));

        assertEquals(rock, board.at(h4).getPiece().orElse(null));
        assertTrue(board.at(b4).getPiece().isEmpty());
    }

    @Test
    void should_move_rook_wrapping_right_to_left() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, g4);

        // g4 to a4 direct is 6 squares, wrapping is 2 (g->h->a)
        card.playOn(board, new PieceToPositionCardParam(rock, a4));

        assertEquals(rock, board.at(a4).getPiece().orElse(null));
    }

    @Test
    void should_reject_pawn() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, h3)));
    }

    @Test
    void should_reject_own_piece_on_destination() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, b4);
        board.add(new Knight(Color.WHITE), h4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, h4)));
    }

    @Test
    void should_capture_enemy_piece() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, b4);
        Knight enemy = new Knight(Color.BLACK);
        board.add(enemy, h4);

        card.playOn(board, new PieceToPositionCardParam(rock, h4));

        assertEquals(rock, board.at(h4).getPiece().orElse(null));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_move_knight_wrapping() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, g4);

        // Knight from g4 normally goes to h6, f6, h2, f2, e3, e5
        // Cylindrically it could also go to a5, a3 (wrapping g+2=i->a)
        card.playOn(board, new PieceToPositionCardParam(knight, a5));

        assertEquals(knight, board.at(a5).getPiece().orElse(null));
    }

    @Test
    void should_reject_enemy_piece() {
        Rock rock = new Rock(Color.BLACK);
        board.add(rock, b4);

        assertThrows(RuntimeException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, h4)));
    }

    @Test
    void should_reject_if_path_blocked_on_wrap() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, b4);
        // Block the wrapping path at a4
        board.add(new Pawn(Color.BLACK), a4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, h4)));
    }
}
