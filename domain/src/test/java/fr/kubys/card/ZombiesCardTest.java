package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ZombiesCardTest {

    ChessBoard board;
    ZombiesCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ZombiesCard();
    }

    @Test
    void should_place_captured_pawn_on_second_rank() {
        WhitePawn pawn = new WhitePawn();
        board.add(pawn, a2);
        board.removePieceFromTheBoard(pawn);

        card.playOn(board, new PieceToPositionCardParam(pawn, d2));

        assertEquals(pawn, board.at(d2).getPiece().get());
    }

    @Test
    void should_reject_wrong_rank() {
        WhitePawn pawn = new WhitePawn();
        board.add(pawn, a2);
        board.removePieceFromTheBoard(pawn);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d3)));
    }

    @Test
    void should_reject_occupied_square() {
        WhitePawn pawn = new WhitePawn();
        board.add(pawn, a2);
        board.removePieceFromTheBoard(pawn);
        board.add(new Knight(Color.WHITE), d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d2)));
    }
}
