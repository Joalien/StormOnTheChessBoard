package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ShotCardTest {

    ChessBoard board;
    ShotCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ShotCard();
    }

    @Test
    void should_remove_enemy_piece_without_moving() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);
        Knight enemy = new Knight(Color.BLACK);
        board.add(enemy, a7);

        card.playOn(board, new PieceToPositionCardParam(rook, a7));

        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
        assertEquals(rook, board.at(a1).getPiece().get()); // shooter stays
        assertTrue(board.at(a7).getPiece().isEmpty());
    }

    @Test
    void should_reject_empty_target() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rook, a5)));
    }

    @Test
    void should_reject_own_piece_as_target() {
        Rock rook = new Rock(Color.WHITE);
        Knight knight = new Knight(Color.WHITE);
        board.add(rook, a1);
        board.add(knight, a3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rook, a3)));
    }

    @Test
    void should_reject_if_piece_cannot_reach_target() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);
        Knight enemy = new Knight(Color.BLACK);
        board.add(enemy, b3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rook, b3)));
    }
}
