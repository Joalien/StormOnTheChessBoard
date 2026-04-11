package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoxCardTest {

    ChessBoard board;
    BoxCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new BoxCard();
    }

    @Test
    void should_swap_enemy_knight_and_rook() {
        Knight knight = new Knight(Color.BLACK);
        Rock rook = new Rock(Color.BLACK);
        board.add(knight, b8);
        board.add(rook, a8);

        card.playOn(board, new TwoPieceCardParam(knight, rook));

        assertInstanceOf(Rock.class, board.at(b8).getPiece().get());
        assertInstanceOf(Knight.class, board.at(a8).getPiece().get());
    }

    @Test
    void should_reject_own_pieces() {
        Knight knight = new Knight(Color.WHITE);
        Rock rook = new Rock(Color.WHITE);
        board.add(knight, b1);
        board.add(rook, a1);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new TwoPieceCardParam(knight, rook)));
    }

    @Test
    void should_reject_wrong_piece_types() {
        Bishop bishop = new Bishop(Color.BLACK);
        Rock rook = new Rock(Color.BLACK);
        board.add(bishop, c8);
        board.add(rook, a8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(bishop, rook)));
    }

    @Test
    void should_reject_if_swap_creates_check_on_own_king() {
        // knight on e5, rook on a5: swap puts rook on e5 which attacks white king on e1
        Knight knight = new Knight(Color.BLACK);
        Rock rook = new Rock(Color.BLACK);
        board.add(knight, e5);
        board.add(rook, a5);

        assertThrows(CheckException.class,
                () -> card.playOn(board, new TwoPieceCardParam(knight, rook)));
    }
}
