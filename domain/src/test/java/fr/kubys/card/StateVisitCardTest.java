package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class StateVisitCardTest {

    ChessBoard board;
    StateVisitCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new StateVisitCard();
    }

    @Test
    void should_swap_kings() {
        card.playOn(board, new NoCardParam());

        Piece onE1 = board.at(e1).getPiece().get();
        Piece onE8 = board.at(e8).getPiece().get();
        assertEquals(Color.BLACK, onE1.getColor());
        assertEquals(Color.WHITE, onE8.getColor());
        assertTrue(onE1.isKing());
        assertTrue(onE8.isKing());
    }

    @Test
    void should_reject_if_swap_creates_check_on_own_king() {
        // Black rook on a8 attacks row 8. After swap, white king goes to e8
        // and black rook on a8 can attack e8? No, rook is same color.
        // Need: white piece attacking e8. White rook on a8... can't, same color as king swapping there.
        // Actually: after swap, white king is on e8. Enemy pieces (black) that attack e8 would check.
        // Black bishop on d7 attacks e8. After swap black king goes to e1.
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Bishop blackBishop = new Bishop(Color.BLACK);
        board.add(blackBishop, d7);
        board.setTurn(Color.WHITE);

        // After swap: white king on e8, black pieces: king on e1, bishop on d7
        // d7 bishop (black) attacks e8 — but white king is there. Black bishop attacks white king = check!
        assertThrows(CheckException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_reject_if_swap_creates_check_on_enemy_king() {
        // After swap, black king goes to e1. White pieces that attack e1 would check.
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Bishop whiteBishop = new Bishop(Color.WHITE);
        board.add(whiteBishop, d2);
        board.setTurn(Color.WHITE);

        // After swap: black king on e1, white pieces: king on e8, bishop on d2
        // d2 bishop (white) attacks e1 — black king there = check!
        assertThrows(CheckException.class,
                () -> card.playOn(board, new NoCardParam()));
    }
}
