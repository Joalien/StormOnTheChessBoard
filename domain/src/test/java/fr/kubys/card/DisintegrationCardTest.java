package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.CheckException;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class DisintegrationCardTest {

    ChessBoard board;
    DisintegrationCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new DisintegrationCard();
    }

    @Test
    void should_remove_own_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);

        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.at(b1).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
    }

    @Test
    void should_reject_removing_enemy_piece() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, b8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_reject_removing_king() {
        King whiteKing = (King) board.at(e1).getPiece().get();

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(whiteKing)));
    }

    @Test
    void should_reject_if_removal_exposes_own_king_to_check() {
        // White rook on e4 shields white king on e1 from black rook on e7
        Rock whiteRook = new Rock(Color.WHITE);
        board.add(whiteRook, e4);
        Rock blackRook = new Rock(Color.BLACK);
        board.add(blackRook, e7);

        assertThrows(CheckException.class,
                () -> card.playOn(board, new PieceCardParam(whiteRook)));
    }
}
