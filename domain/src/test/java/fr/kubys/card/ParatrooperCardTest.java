package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParatrooperCardTest {

    ChessBoard board;
    ParatrooperCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ParatrooperCard();
    }

    @Test
    void should_parachute_captured_pawn_to_center() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a2);
        board.removePieceFromTheBoard(pawn);

        card.playOn(board, new PieceToPositionCardParam(pawn, d4));

        assertEquals(pawn, board.at(d4).getPiece().get());
    }

    @Test
    void should_reject_non_center_square() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a2);
        board.removePieceFromTheBoard(pawn);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, a4)));
    }

    @Test
    void should_reject_occupied_center() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a2);
        board.removePieceFromTheBoard(pawn);
        board.add(new Knight(Color.BLACK), d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, d4)));
    }

    @Test
    void should_reject_non_pawn() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, a2);
        board.removePieceFromTheBoard(knight);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, d4)));
    }
}
