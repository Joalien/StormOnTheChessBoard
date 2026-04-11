package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CannibalCardTest {

    ChessBoard board;
    CannibalCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CannibalCard();
    }

    @Test
    void should_eat_own_piece() {
        Rock rook = new Rock(Color.WHITE);
        Knight knight = new Knight(Color.WHITE);
        board.add(rook, a1);
        board.add(knight, a3);

        card.playOn(board, new PieceToPositionCardParam(rook, a3));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertEquals(rook, board.at(a3).getPiece().get());
        assertTrue(board.at(a1).getPiece().isEmpty());
    }

    @Test
    void should_reject_eating_enemy_piece() {
        Rock rook = new Rock(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(rook, a1);
        board.add(enemy, a3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rook, a3)));
    }

    @Test
    void should_reject_eating_king() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rook, e1)));
    }

    @Test
    void should_eat_neutral_piece() {
        Rock rook = new Rock(Color.WHITE);
        Knight neutral = new Knight(Color.BLACK);
        board.add(rook, a1);
        board.add(neutral, a3);
        neutral.setColor(fr.kubys.core.Color.NONE);

        card.playOn(board, new PieceToPositionCardParam(rook, a3));

        assertTrue(board.getOutOfTheBoardPieces().contains(neutral));
        assertEquals(rook, board.at(a3).getPiece().get());
    }

    @Test
    void should_allow_king_as_cannibal() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d1);
        Piece king = board.at(e1).getPiece().get();

        card.playOn(board, new PieceToPositionCardParam(king, d1));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertEquals(king, board.at(d1).getPiece().get());
    }

    @Test
    void should_reject_empty_target() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rook, a5)));
    }
}
