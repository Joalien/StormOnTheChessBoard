package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PrivateJetCardTest {

    ChessBoard board;
    PrivateJetCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new PrivateJetCard();
    }

    @Test
    void should_move_king_to_any_free_square() {
        card.playOn(board, new PositionCardParam(a5));

        assertTrue(board.at(e1).getPiece().isEmpty());
        Piece king = board.at(a5).getPiece().get();
        assertTrue(king.isKing());
        assertEquals(Color.WHITE, king.getColor());
    }

    @Test
    void should_reject_occupied_square() {
        board.add(new Knight(Color.WHITE), d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PositionCardParam(d4)));
    }

    @Test
    void should_reject_if_creates_check() {
        board.add(new Rock(Color.BLACK), a5);

        assertThrows(CheckException.class,
                () -> card.playOn(board, new PositionCardParam(a3)));
    }
}
