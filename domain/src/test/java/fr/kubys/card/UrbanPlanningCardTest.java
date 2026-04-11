package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrbanPlanningCardTest {

    ChessBoard board;
    UrbanPlanningCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new UrbanPlanningCard();
    }

    @Test
    void should_swap_own_and_enemy_piece() {
        Rock own = new Rock(Color.WHITE);
        Rock enemy = new Rock(Color.BLACK);
        board.add(own, h3);
        board.add(enemy, h6);

        card.playOn(board, new TwoPieceCardParam(own, enemy));

        assertEquals(Color.WHITE, board.at(h6).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(h3).getPiece().get().getColor());
    }

    @Test
    void should_reject_two_own_pieces() {
        Rock p1 = new Rock(Color.WHITE);
        Rock p2 = new Rock(Color.WHITE);
        board.add(p1, c1);
        board.add(p2, f1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(p1, p2)));
    }

    @Test
    void should_reject_wrong_types() {
        Queen q = new Queen(Color.WHITE);
        Rock p = new Rock(Color.BLACK);
        board.add(q, d1);
        board.add(p, c8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(q, p)));
    }
}
