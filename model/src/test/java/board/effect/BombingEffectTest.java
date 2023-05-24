package board.effect;

import board.ChessBoard;
import board.effect.BombingEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import core.Color;
import piece.King;
import piece.Queen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static core.Position.d4;
import static core.Position.e4;

class BombingEffectTest {

    private ChessBoard chessBoard;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        chessBoard.addEffect(new BombingEffect(e4, Color.WHITE));
    }

    @Nested
    class Success {
        @Test
        void should_work_if_played_ally_piece_moves_on_it() {
            Queen queen = new Queen(Color.WHITE);

            chessBoard.add(queen, e4);

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
            assertEquals(1, chessBoard.getEffects().size());
        }

        @Test
        void should_explode_enemy_piece() {
            Queen queen = new Queen(Color.BLACK);

            chessBoard.add(queen, e4);

            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
            assertTrue(chessBoard.getEffects().isEmpty());
        }

        @Test
        void should_explode_enemy_piece_only_once() {
            Queen queen = new Queen(Color.BLACK);
            assertEquals(1, chessBoard.getEffects().size());

            chessBoard.add(queen, e4);

            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
            assertTrue(chessBoard.getEffects().isEmpty());

            chessBoard.add(queen, e4);

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
            assertTrue(chessBoard.getEffects().isEmpty());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_explode_if_enemy_king_but_does_not_hurt_it() {
            King king = new King(Color.BLACK);

            chessBoard.add(king, e4);

            assertEquals(king, chessBoard.at(e4).getPiece().get());
            assertTrue(chessBoard.getEffects().isEmpty());
        }

        @Test
        void should_not_explode_if_enemy_piece_move_nearby() {
            Queen queen = new Queen(Color.BLACK);

            chessBoard.add(queen, d4);

            assertEquals(queen, chessBoard.at(d4).getPiece().get());
            assertEquals(1, chessBoard.getEffects().size());
        }
    }
}