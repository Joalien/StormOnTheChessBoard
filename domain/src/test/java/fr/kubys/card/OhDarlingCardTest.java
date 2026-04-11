package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class OhDarlingCardTest {

    ChessBoard board;
    OhDarlingCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new OhDarlingCard();
    }

    @Test
    void should_move_king_next_to_queen() {
        Queen queen = new Queen(Color.WHITE);
        board.add(queen, a5);
        Piece king = board.at(e1).getPiece().get();

        card.playOn(board, new PieceToPositionCardParam(king, b5));

        assertTrue(king.isKing());
        assertEquals(king, board.at(b5).getPiece().get());
    }

    @Test
    void should_move_queen_next_to_king() {
        Queen queen = new Queen(Color.WHITE);
        board.add(queen, h8);

        card.playOn(board, new PieceToPositionCardParam(queen, d1));

        assertEquals(queen, board.at(d1).getPiece().get());
    }

    @Test
    void should_reject_non_adjacent_target() {
        Queen queen = new Queen(Color.WHITE);
        board.add(queen, a5);
        Piece king = board.at(e1).getPiece().get();

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(king, c5)));
    }

    @Test
    void should_reject_occupied_target() {
        Queen queen = new Queen(Color.WHITE);
        board.add(queen, d2);
        board.add(new Knight(Color.WHITE), d1);
        Piece king = board.at(e1).getPiece().get();

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(king, d1)));
    }
}
