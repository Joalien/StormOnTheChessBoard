package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CrazyTowerCardTest {

    ChessBoard board;
    CrazyTowerCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CrazyTowerCard();
    }

    @Test
    void should_swap_enemy_pieces() {
        Bishop p1 = new Bishop(Color.BLACK);
        Rock p2 = new Rock(Color.BLACK);
        board.add(p1, b8);
        board.add(p2, c8);

        card.playOn(board, new TwoPieceCardParam(p1, p2));

        assertInstanceOf(Bishop.class, board.at(c8).getPiece().get());
        assertInstanceOf(Rock.class, board.at(b8).getPiece().get());
    }

    @Test
    void should_reject_own_pieces() {
        Bishop p1 = new Bishop(Color.WHITE);
        Rock p2 = new Rock(Color.WHITE);
        board.add(p1, b1);
        board.add(p2, c1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(p1, p2)));
    }

    @Test
    void should_reject_wrong_types() {
        Queen q = new Queen(Color.BLACK);
        Rock p2 = new Rock(Color.BLACK);
        board.add(q, d8);
        board.add(p2, c8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(q, p2)));
    }
}
