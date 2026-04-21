package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ModestyCardTest {

    ChessBoard board;
    ModestyCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ModestyCard();
    }

    @Test
    void should_move_knight_forward_one_square() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        card.playOn(board, new PieceToPositionCardParam(knight, c4));

        assertEquals(knight, board.at(c4).getPiece().get());
    }

    @Test
    void should_capture_diagonally() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, d4);

        card.playOn(board, new PieceToPositionCardParam(knight, d4));

        assertEquals(knight, board.at(d4).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_reject_backward_move() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, c2)));
    }

    @Test
    void should_reject_forward_onto_occupied() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Pawn ally = new Pawn(Color.WHITE);
        board.add(ally, c4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, c4)));
    }

    @Test
    void should_reject_diagonal_without_capture() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, d4)));
    }

    @Test
    void should_promote_piece_moved_to_last_rank() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, c7);

        card.playOn(board, new PieceToPositionCardParam(rock, c8));

        Piece pieceOnC8 = board.at(c8).getPiece().get();
        assertInstanceOf(Queen.class, pieceOnC8);
        assertEquals(Color.WHITE, pieceOnC8.getColor());
        List<fr.kubys.core.Position> promoted = board.drainPromotedPositions();
        assertTrue(promoted.contains(c8));
    }
}
