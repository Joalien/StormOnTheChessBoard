package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.board.effect.MagnetismEffect;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ImitatorCardTest {

    ChessBoard board;
    ImitatorCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ImitatorCard();
    }

    @Test
    void should_move_piece_like_last_enemy_piece() {
        // Simulate enemy's last move was a knight move
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, b8);
        board.move(enemyKnight, c6);

        // Now white can move a piece like a knight
        Rock whiteRock = new Rock(Color.WHITE);
        board.add(whiteRock, d4);

        card.playOn(board, new PieceToPositionCardParam(whiteRock, e6)); // knight L-shape

        assertEquals(whiteRock, board.at(e6).getPiece().get());
    }

    @Test
    void should_reject_invalid_imitation_move() {
        // Last move was a knight
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, b8);
        board.move(enemyKnight, c6);

        Rock whiteRock = new Rock(Color.WHITE);
        board.add(whiteRock, a3);

        // Try to move rook straight (not knight-like)
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(whiteRock, a6)));
    }

    @Test
    void should_reject_pawn() {
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, b8);
        board.move(enemyKnight, c6);

        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, e4)));
    }

    @Test
    void should_reject_capture() {
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, b8);
        board.move(enemyKnight, c6);

        Rock whiteRock = new Rock(Color.WHITE);
        board.add(whiteRock, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(whiteRock, c6)));
    }

    @Nested
    class HookTriggersIT {

        @Test
        void should_trigger_bombing_afterMoveHook_when_landing_on_bomb() {
            Knight enemyKnight = new Knight(Color.BLACK);
            board.add(enemyKnight, b8);
            board.move(enemyKnight, c6);

            Rock whiteRock = new Rock(Color.WHITE);
            board.add(whiteRock, d4);
            board.addEffect(new BombingEffect(e6, Color.BLACK));

            card.playOn(board, new PieceToPositionCardParam(whiteRock, e6));

            // Bomb should have destroyed the rook via afterMoveHook
            assertTrue(board.getOutOfTheBoardPieces().contains(whiteRock));
            assertTrue(board.getEffects().isEmpty());
        }

        @Test
        void should_not_trigger_bombing_when_landing_on_own_bomb() {
            Knight enemyKnight = new Knight(Color.BLACK);
            board.add(enemyKnight, b8);
            board.move(enemyKnight, c6);

            Rock whiteRock = new Rock(Color.WHITE);
            board.add(whiteRock, d4);
            board.addEffect(new BombingEffect(e6, Color.WHITE));

            card.playOn(board, new PieceToPositionCardParam(whiteRock, e6));

            // Own bomb should not destroy the piece
            assertEquals(whiteRock, board.at(e6).getPiece().get());
            assertFalse(board.getEffects().isEmpty());
        }

        @Test
        void should_remove_magnetism_effect_when_magnetized_piece_is_moved() {
            Knight enemyKnight = new Knight(Color.BLACK);
            board.add(enemyKnight, b8);
            board.move(enemyKnight, c6);

            Rock whiteRock = new Rock(Color.WHITE);
            board.add(whiteRock, d4);
            board.addEffect(new MagnetismEffect(whiteRock));

            card.playOn(board, new PieceToPositionCardParam(whiteRock, e6));

            // Magnetism should be removed via afterMoveHook when magnetized piece moves
            assertEquals(whiteRock, board.at(e6).getPiece().get());
            assertTrue(board.getEffects().isEmpty());
        }

        @Test
        void magnetism_beforeMoveHook_should_not_block_imitator_card() {
            Knight enemyKnight = new Knight(Color.BLACK);
            board.add(enemyKnight, b8);
            board.move(enemyKnight, c6);

            Rock whiteRock = new Rock(Color.WHITE);
            board.add(whiteRock, d4);
            Knight magnetized = new Knight(Color.BLACK);
            board.add(magnetized, d5);
            board.addEffect(new MagnetismEffect(magnetized));

            // Card bypasses beforeMoveHook (uses add() not move()), so magnetism should not block
            assertDoesNotThrow(() -> card.playOn(board, new PieceToPositionCardParam(whiteRock, e6)));
            assertEquals(whiteRock, board.at(e6).getPiece().get());
        }
    }
}
