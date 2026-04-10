package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BreakthroughCardTest {

    ChessBoard board;
    BreakthroughCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new BreakthroughCard();
    }

    @Test
    void should_capture_enemy_piece_straight_ahead() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d4);
        board.add(enemy, d5);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(pawn, board.at(d5).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_reject_if_no_enemy_ahead() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_if_own_piece_ahead() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        board.add(new Knight(Color.WHITE), d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_promote_on_last_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d7);
        board.add(new Knight(Color.BLACK), d8);

        card.playOn(board, new PieceCardParam(pawn));

        assertInstanceOf(Queen.class, board.at(d8).getPiece().get());
    }
}
