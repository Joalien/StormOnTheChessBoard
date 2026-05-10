package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.ShieldEffect;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ShieldCardTest {

    private ChessBoard chessBoard;
    private ShieldCard shield;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        chessBoard.add(new King(Color.WHITE), e1);
        chessBoard.add(new King(Color.BLACK), e8);
        chessBoard.setTurn(Color.WHITE);
        shield = new ShieldCard();
    }

    @Nested
    class Success {
        @Test
        void should_add_shield_effect_after_move() {
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, b1);
            chessBoard.move(knight, c3);

            shield.playOn(chessBoard, new NoCardParam());

            assertEquals(1, chessBoard.getEffects().size());
            assertTrue(chessBoard.getEffects().stream().anyMatch(e -> e instanceof ShieldEffect));
        }

        @Test
        void should_block_enemy_from_capturing_shielded_piece() {
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, b1);
            chessBoard.move(knight, c3);

            shield.playOn(chessBoard, new NoCardParam());

            Queen enemyQueen = new Queen(Color.BLACK);
            chessBoard.add(enemyQueen, c7);
            assertFalse(chessBoard.canAttack(enemyQueen, c3));
        }

        @Test
        void should_expire_shield_after_opponent_move() {
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, b1);
            chessBoard.move(knight, c3);

            shield.playOn(chessBoard, new NoCardParam());
            assertTrue(chessBoard.getEffects().stream().anyMatch(e -> e instanceof ShieldEffect));

            // Opponent's single turn: as soon as their move is committed the shield expires.
            chessBoard.setTurn(Color.BLACK);
            Pawn enemyPawn = new Pawn(Color.BLACK);
            chessBoard.add(enemyPawn, a7);
            chessBoard.move(enemyPawn, a6);

            assertFalse(chessBoard.getEffects().stream().anyMatch(e -> e instanceof ShieldEffect),
                    "Shield should be gone immediately after the opponent's move, without waiting for the owner to act");
        }

        @Test
        void should_allow_shielded_piece_to_move_away() {
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, b1);
            chessBoard.move(knight, c3);

            shield.playOn(chessBoard, new NoCardParam());

            // The shielded piece itself should still be able to move
            assertTrue(chessBoard.getLegalMoves(knight).contains(d5) || chessBoard.getLegalMoves(knight).contains(e4));
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throw_when_no_move_was_made() {
            assertThrows(IllegalStateException.class, () -> shield.playOn(chessBoard, new NoCardParam()));
        }
    }
}
