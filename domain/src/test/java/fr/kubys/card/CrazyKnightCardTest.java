package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CrazyKnightCardTest {

    ChessBoard board;
    CrazyKnightCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CrazyKnightCard();
    }

    @Test
    void should_swap_own_pieces() {
        Bishop p1 = new Bishop(Color.WHITE);
        Knight p2 = new Knight(Color.WHITE);
        board.add(p1, c1);
        board.add(p2, b1);

        card.playOn(board, new TwoPieceCardParam(p1, p2));

        assertInstanceOf(Bishop.class, board.at(b1).getPiece().get());
        assertInstanceOf(Knight.class, board.at(c1).getPiece().get());
    }

    @Test
    void should_reject_enemy_pieces() {
        Bishop p1 = new Bishop(Color.BLACK);
        Knight p2 = new Knight(Color.BLACK);
        board.add(p1, c8);
        board.add(p2, b8);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new TwoPieceCardParam(p1, p2)));
    }

    @Test
    void should_reject_wrong_types() {
        Queen q = new Queen(Color.WHITE);
        Knight p2 = new Knight(Color.WHITE);
        board.add(q, d1);
        board.add(p2, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(q, p2)));
    }
}
