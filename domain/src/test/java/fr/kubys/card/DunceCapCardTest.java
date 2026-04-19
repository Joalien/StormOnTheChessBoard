package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BlackHoleEffect;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.board.effect.DunceCapEffect;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class DunceCapCardTest {

    ChessBoard board;
    DunceCapCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new DunceCapCard();
    }

    @Test
    void should_send_enemy_piece_to_corner() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);

        card.playOn(board, new PieceToPositionCardParam(knight, a1));

        assertEquals(knight, board.at(a1).getPiece().get());
        assertTrue(board.at(d5).getPiece().isEmpty());
    }

    @Test
    void should_add_dunce_cap_effect() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);

        card.playOn(board, new PieceToPositionCardParam(knight, a1));

        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof DunceCapEffect));
    }

    @Test
    void should_freeze_piece_on_next_turn() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);

        card.playOn(board, new PieceToPositionCardParam(knight, a1));

        // Simulate end of white's turn, now it's black's turn
        board.setTurn(Color.BLACK);

        // Black tries to move the frozen piece
        assertThrows(IllegalStateException.class,
                () -> board.move(knight, b3));
    }

    @Test
    void should_allow_piece_to_move_after_freeze_expires() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);

        card.playOn(board, new PieceToPositionCardParam(knight, a1));

        // White's turn ends
        board.setTurn(Color.BLACK);
        // Black's turn ends (without moving the frozen piece)
        board.setTurn(Color.WHITE);
        // White's turn ends
        board.setTurn(Color.BLACK);

        // Now the freeze should have expired, piece can move
        assertDoesNotThrow(() -> board.move(knight, b3));
        assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof DunceCapEffect));
    }

    @Test
    void should_reject_king() {
        King king = (King) board.at(e8).getPiece().get();

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(king, a1)));
    }

    @Test
    void should_reject_own_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, a1)));
    }

    @Test
    void should_reject_non_corner_destination() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, a4)));
    }

    @Test
    void should_reject_occupied_corner() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);
        board.add(new Rock(Color.WHITE), a1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, a1)));
    }

    @Test
    void should_reject_when_creates_check() {
        // Black rook on e5 blocks nothing, but if we move it to a corner,
        // it could uncover an attack. Setup: white king e1, black bishop a5 (diagonal blocked by black rook d4)
        // Moving rook d4 to a1 opens diagonal a5-e1 → check
        Rock enemyRook = new Rock(Color.BLACK);
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyRook, d4);
        board.add(enemyBishop, a5);

        // This is blocked? No, let me think. Black rook on d4, black bishop on a5.
        // Diagonal a5→e1: a5, b4, c3, d2, e1. Rook is on d4 which is NOT on this diagonal.
        // Let me fix: put blocker on b4. Rook on b4 blocks diagonal a5-e1.
        board.removePieceFromTheBoard(enemyRook);
        board.add(enemyRook, b4);

        // Moving rook from b4 to h1 opens diagonal a5→e1 → check
        assertThrows(CheckException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyRook, h1)));
    }

    @Test
    void should_reject_when_corner_has_black_hole() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);
        board.addEffect(new BlackHoleEffect(a1));

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, a1)));
    }

    @Test
    void should_trigger_bombing_effect_when_landing_on_bombed_corner() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);
        board.addEffect(new BombingEffect(h1, Color.WHITE));

        card.playOn(board, new PieceToPositionCardParam(knight, h1));

        // The bombing destroys the enemy piece that lands on the bombed square
        assertNull(knight.getPosition());
        assertTrue(board.at(h1).getPiece().isEmpty());
        // Both the bombing effect and the dunce cap effect should be gone (piece was removed)
        assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof BombingEffect));
        assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof DunceCapEffect));
    }

    @Test
    void should_not_trigger_bombing_if_played_by_opponent() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d5);
        // Bombing played by Black — should NOT trigger on a black piece
        board.addEffect(new BombingEffect(h1, Color.BLACK));

        card.playOn(board, new PieceToPositionCardParam(knight, h1));

        // The knight survives because the bomb only detonates for enemy pieces
        assertEquals(knight, board.at(h1).getPiece().get());
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof BombingEffect));
    }
}
