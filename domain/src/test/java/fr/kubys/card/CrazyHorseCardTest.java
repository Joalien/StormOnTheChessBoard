package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CrazyHorseCardTest {

    ChessBoard board;
    CrazyHorseCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new CrazyHorseCard();
    }

    @Test
    void should_swap_enemy_pieces() {
        Knight p1 = new Knight(Color.BLACK);
        Bishop p2 = new Bishop(Color.BLACK);
        board.add(p1, b8);
        board.add(p2, c8);

        card.playOn(board, new TwoPieceCardParam(p1, p2));

        assertInstanceOf(Knight.class, board.at(c8).getPiece().get());
        assertInstanceOf(Bishop.class, board.at(b8).getPiece().get());
    }

    @Test
    void should_reject_own_pieces() {
        Knight p1 = new Knight(Color.WHITE);
        Bishop p2 = new Bishop(Color.WHITE);
        board.add(p1, b1);
        board.add(p2, c1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(p1, p2)));
    }

    @Test
    void should_reject_wrong_types() {
        Queen q = new Queen(Color.BLACK);
        Bishop p2 = new Bishop(Color.BLACK);
        board.add(q, d8);
        board.add(p2, c8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(q, p2)));
    }
}
