package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FunnyCardTest {

    ChessBoard board;
    FunnyCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new FunnyCard();
    }

    @Test
    void should_capture_backward_diagonally() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d4);
        board.add(enemy, c3);

        card.playOn(board, new PieceToPositionCardParam(pawn, c3));

        assertEquals(pawn, board.at(c3).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_capture_backward_to_first_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, b2);
        board.add(enemy, a1);

        card.playOn(board, new PieceToPositionCardParam(pawn, a1));

        // Pawn lands on row 1 — NOT its promotion row (promotion is row 8 for white)
        assertInstanceOf(Pawn.class, board.at(a1).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_reject_forward_capture() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        board.add(new Knight(Color.BLACK), c5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, c5)));
    }

    @Test
    void should_reject_empty_target() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, c3)));
    }
}
