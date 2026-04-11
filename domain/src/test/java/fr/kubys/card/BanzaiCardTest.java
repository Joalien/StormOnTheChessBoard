package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BanzaiCardTest {

    ChessBoard board;
    BanzaiCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new BanzaiCard();
    }

    @Test
    void should_advance_white_pawn_three_squares() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(pawn, board.at(d5).getPiece().get());
        assertTrue(board.at(d2).getPiece().isEmpty());
    }

    @Test
    void should_advance_black_pawn_three_squares() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d7);
        board.setTurn(Color.BLACK);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(pawn, board.at(d4).getPiece().get());
    }

    @Test
    void should_reject_if_path_blocked() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);
        board.add(new Knight(Color.WHITE), d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_if_would_go_off_board() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_promote_if_reaching_last_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d5);

        card.playOn(board, new PieceCardParam(pawn));

        assertInstanceOf(Queen.class, board.at(d8).getPiece().get());
    }
}
