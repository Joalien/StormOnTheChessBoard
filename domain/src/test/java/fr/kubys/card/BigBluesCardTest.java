package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BigBluesCardTest {

    ChessBoard board;
    BigBluesCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new BigBluesCard();
    }

    @Test
    void should_remove_isolated_enemy_pawn() {
        BlackPawn pawn = new BlackPawn();
        board.add(pawn, a6);

        card.playOn(board, new PieceCardParam(pawn));

        assertTrue(board.at(a6).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(pawn));
    }

    @Test
    void should_reject_if_neighbor_is_not_empty() {
        BlackPawn pawn = new BlackPawn();
        board.add(pawn, d5);
        board.add(new Knight(Color.WHITE), c4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_own_pawn() {
        WhitePawn pawn = new WhitePawn();
        board.add(pawn, a3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_non_pawn() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, a6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }
}
