package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BestFriendsCardTest {

    ChessBoard board;
    BestFriendsCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new BestFriendsCard();
    }

    @Test
    void should_swap_queens() {
        Queen whiteQueen = new Queen(Color.WHITE);
        Queen blackQueen = new Queen(Color.BLACK);
        board.add(whiteQueen, h3);
        board.add(blackQueen, h6);

        card.playOn(board, new NoCardParam());

        assertEquals(Color.WHITE, board.at(h6).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(h3).getPiece().get().getColor());
    }

    @Test
    void should_reject_if_no_own_queen() {
        board.add(new Queen(Color.BLACK), d8);

        assertThrows(IllegalStateException.class,
                () -> card.playOn(board, new NoCardParam()));
    }

    @Test
    void should_reject_if_no_enemy_queen() {
        board.add(new Queen(Color.WHITE), d1);

        assertThrows(IllegalStateException.class,
                () -> card.playOn(board, new NoCardParam()));
    }
}
