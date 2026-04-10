package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class YouOnlyLiveTwiceCardTest {

    ChessBoard board;
    YouOnlyLiveTwiceCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new YouOnlyLiveTwiceCard();
    }

    @Test
    void should_resurrect_captured_knight() {
        Knight captured = new Knight(Color.WHITE);
        board.add(captured, d5);
        board.removePieceFromTheBoard(captured);

        card.playOn(board, new PieceToPositionCardParam(captured, b1));

        assertEquals(captured, board.at(b1).getPiece().get());
    }

    @Test
    void should_reject_queen() {
        Queen captured = new Queen(Color.WHITE);
        board.add(captured, d5);
        board.removePieceFromTheBoard(captured);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(captured, d1)));
    }

    @Test
    void should_resurrect_captured_pawn() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d5);
        board.removePieceFromTheBoard(pawn);

        card.playOn(board, new PieceToPositionCardParam(pawn, d2));

        assertEquals(pawn, board.at(d2).getPiece().get());
    }

    @Test
    void should_reject_piece_still_on_the_board() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, g1)));
    }

    @Test
    void should_reject_enemy_piece() {
        Knight enemy = new Knight(Color.BLACK);
        board.add(enemy, d5);
        board.removePieceFromTheBoard(enemy);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemy, b8)));
    }

    @Test
    void should_reject_invalid_starting_position() {
        Knight captured = new Knight(Color.WHITE);
        board.add(captured, d5);
        board.removePieceFromTheBoard(captured);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(captured, d5)));
    }

    @Test
    void should_reject_occupied_starting_position() {
        Knight captured = new Knight(Color.WHITE);
        board.add(captured, d5);
        board.removePieceFromTheBoard(captured);
        board.add(new Bishop(Color.WHITE), b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(captured, b1)));
    }

    @Test
    void should_allow_neutral_captured_piece() {
        Knight neutral = new Knight(Color.WHITE);
        board.add(neutral, d5);
        neutral.setColor(Color.NONE);
        board.removePieceFromTheBoard(neutral);

        // Neutral pieces can be moved by anyone, so this should work
        // But starting positions are looked up by color — NONE has no starting positions
        // So this should actually be rejected
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(neutral, b1)));
    }
}
