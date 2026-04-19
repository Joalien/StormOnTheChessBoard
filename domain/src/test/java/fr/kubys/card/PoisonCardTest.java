package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.PoisonEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PoisonCardTest {

    ChessBoard board;
    PoisonCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new PoisonCard();
    }

    @Test
    void should_poison_own_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof PoisonEffect));
    }

    @Test
    void should_remove_both_pieces_when_poisoned_piece_is_captured() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        card.playOn(board, new PieceCardParam(knight));

        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, a5);
        board.move(enemyBishop, c3);

        assertTrue(board.at(c3).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyBishop));
        assertFalse(board.getEffects().stream().anyMatch(e -> e instanceof PoisonEffect));
    }

    @Test
    void should_not_trigger_when_poisoned_piece_moves_normally() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        card.playOn(board, new PieceCardParam(knight));

        board.move(knight, d5);

        assertTrue(board.at(d5).getPiece().isPresent());
        assertEquals(knight, board.at(d5).getPiece().get());
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof PoisonEffect));
    }

    @Test
    void should_not_trigger_when_friendly_piece_captures_poisoned_piece_position() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        card.playOn(board, new PieceCardParam(pawn));

        // Remove the pawn manually (simulate some other removal)
        board.removePieceFromTheBoard(pawn);

        // A friendly piece moves to that position - should NOT be removed
        Knight friendlyKnight = new Knight(Color.WHITE);
        board.add(friendlyKnight, d4);

        assertTrue(board.at(d4).getPiece().isPresent());
        assertEquals(friendlyKnight, board.at(d4).getPiece().get());
    }

    @Test
    void should_reject_poisoning_enemy_piece() {
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, c6);

        assertThrows(CannotMoveThisColorException.class, () -> card.playOn(board, new PieceCardParam(enemyKnight)));
    }

    @Test
    void should_reject_poisoning_king() {
        King king = (King) board.at(e1).getPiece().get();

        assertThrows(IllegalArgumentException.class, () -> card.playOn(board, new PieceCardParam(king)));
    }

    @Test
    void should_track_poisoned_piece_when_it_moves_then_gets_captured() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        card.playOn(board, new PieceCardParam(knight));

        // Move poisoned piece
        board.move(knight, e4);

        // Enemy captures at new position
        fr.kubys.piece.Rock enemyRock = new fr.kubys.piece.Rock(Color.BLACK);
        board.add(enemyRock, e7);
        board.move(enemyRock, e4);

        assertTrue(board.at(e4).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyRock));
        assertFalse(board.getEffects().stream().anyMatch(e -> e instanceof PoisonEffect));
    }

    @Test
    void should_not_trigger_when_unrelated_piece_is_captured() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        card.playOn(board, new PieceCardParam(knight));

        // A different white piece gets captured
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d4);
        fr.kubys.piece.Rock enemyRock = new fr.kubys.piece.Rock(Color.BLACK);
        board.add(enemyRock, d7);
        board.move(enemyRock, d4);

        // Rock should still be on the board (only pawn was captured, not the poisoned knight)
        assertTrue(board.at(d4).getPiece().isPresent());
        assertEquals(enemyRock, board.at(d4).getPiece().get());
        // Poisoned knight should still be on the board
        assertTrue(board.at(c3).getPiece().isPresent());
        // Effect should still be active
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof PoisonEffect));
    }

    @Test
    void should_not_trigger_when_poisoned_piece_destroyed_by_card_then_enemy_moves() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        card.playOn(board, new PieceCardParam(knight));

        // Enemy destroys the poisoned piece via a card effect (not a capture)
        board.removePieceFromTheBoard(knight);
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));

        // Enemy moves one of their pieces — it should NOT be destroyed
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyRook, a7);
        board.move(enemyRook, a5);

        assertTrue(board.at(a5).getPiece().isPresent());
        assertEquals(enemyRook, board.at(a5).getPiece().get());
    }
}
