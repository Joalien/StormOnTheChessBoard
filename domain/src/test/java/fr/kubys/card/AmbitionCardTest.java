package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class AmbitionCardTest {

    ChessBoard board;
    AmbitionCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new AmbitionCard();
    }

    @Test
    void should_replace_pawn_with_captured_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        Knight captured = new Knight(Color.WHITE);
        board.add(captured, a3);
        board.removePieceFromTheBoard(captured);

        card.playOn(board, new PieceToPositionCardParam(captured, d4));

        assertEquals(captured, board.at(d4).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(pawn));
    }

    @Test
    void should_reject_queen_resurrection() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        Queen captured = new Queen(Color.WHITE);
        board.add(captured, a3);
        board.removePieceFromTheBoard(captured);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(captured, d4)));
    }

    @Test
    void should_reject_piece_still_on_the_board() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        Knight notCaptured = new Knight(Color.WHITE);
        board.add(notCaptured, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(notCaptured, d4)));
    }

    @Test
    void should_allow_neutral_captured_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        Knight neutral = new Knight(Color.WHITE);
        board.add(neutral, a3);
        neutral.setColor(Color.NONE);
        board.removePieceFromTheBoard(neutral);

        card.playOn(board, new PieceToPositionCardParam(neutral, d4));

        assertEquals(neutral, board.at(d4).getPiece().get());
    }

    @Test
    void should_reject_enemy_captured_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        Knight enemyPiece = new Knight(Color.BLACK);
        board.add(enemyPiece, a6);
        board.removePieceFromTheBoard(enemyPiece);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyPiece, d4)));
    }

    @Test
    void should_reject_if_target_is_not_pawn() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Bishop captured = new Bishop(Color.WHITE);
        board.add(captured, a3);
        board.removePieceFromTheBoard(captured);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(captured, d4)));
    }
}
