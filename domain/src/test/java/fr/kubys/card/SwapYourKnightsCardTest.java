package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SwapYourKnightsCardTest {

    ChessBoard board;
    SwapYourKnightsCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new SwapYourKnightsCard();
    }

    @Test
    void should_swap_own_and_enemy_piece() {
        Knight own = new Knight(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(own, c1);
        board.add(enemy, c8);

        card.playOn(board, new TwoPieceCardParam(own, enemy));

        assertEquals(Color.WHITE, board.at(c8).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(c1).getPiece().get().getColor());
    }

    @Test
    void should_reject_two_own_pieces() {
        Knight p1 = new Knight(Color.WHITE);
        Knight p2 = new Knight(Color.WHITE);
        board.add(p1, c1);
        board.add(p2, f1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(p1, p2)));
    }

    @Test
    void should_reject_wrong_types() {
        Queen q = new Queen(Color.WHITE);
        Knight p = new Knight(Color.BLACK);
        board.add(q, d1);
        board.add(p, c8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(q, p)));
    }
}
