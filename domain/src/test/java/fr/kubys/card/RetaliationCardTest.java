package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class RetaliationCardTest {

    ChessBoard board;
    RetaliationCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new RetaliationCard();
    }

    @Test
    void should_remove_enemy_pawn_after_non_pawn_capture() {
        // Simulate white capturing a black knight this turn
        Knight capturedKnight = new Knight(Color.BLACK);
        board.add(capturedKnight, d5);
        Rock whiteRook = new Rock(Color.WHITE);
        board.add(whiteRook, d1);
        board.tryToMove(d1, d5); // white rook captures black knight

        WhitePawn pawn = new WhitePawn();
        board.add(pawn, a2);

        card.playOn(board, new PieceCardParam(pawn));

        assertTrue(board.at(a2).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(pawn));
    }

    @Test
    void should_reject_own_pawn() {
        BlackPawn pawn = new BlackPawn();
        board.add(pawn, a7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_non_pawn() {
        Knight capturedKnight = new Knight(Color.BLACK);
        board.add(capturedKnight, d5);
        Rock whiteRook = new Rock(Color.WHITE);
        board.add(whiteRook, d1);
        board.tryToMove(d1, d5);

        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(rook)));
    }

    @Test
    void should_reject_if_no_capture_this_turn() {
        WhitePawn pawn = new WhitePawn();
        board.add(pawn, a2);

        assertThrows(IllegalStateException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_if_only_pawn_was_captured() {
        // White captures a black pawn (not a piece)
        BlackPawn capturedPawn = new BlackPawn();
        board.add(capturedPawn, d5);
        Rock whiteRook = new Rock(Color.WHITE);
        board.add(whiteRook, d1);
        board.tryToMove(d1, d5);

        WhitePawn targetPawn = new WhitePawn();
        board.add(targetPawn, a2);

        assertThrows(IllegalStateException.class,
                () -> card.playOn(board, new PieceCardParam(targetPawn)));
    }
}
