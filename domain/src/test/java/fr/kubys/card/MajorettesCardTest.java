package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MajorettesCardTest {

    ChessBoard board;
    MajorettesCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new MajorettesCard();
    }

    @Test
    void should_move_two_pawns_laterally() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, c4);
        board.add(p2, f4);

        card.playOn(board, new TwoPieceCardParam(p1, p2));

        assertTrue(board.at(c4).getPiece().isEmpty());
        assertTrue(board.at(f4).getPiece().isEmpty());
    }

    @Test
    void should_reject_non_pawns() {
        Knight k = new Knight(Color.WHITE);
        Pawn p = new Pawn(Color.WHITE);
        board.add(k, b1);
        board.add(p, c4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(k, p)));
    }
}
