package board.effect;

import board.ChessBoard;
import board.effect.MagnetismEffect;
import board.effect.MagnetismException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import core.Color;
import piece.Queen;

import static org.junit.jupiter.api.Assertions.*;
import static core.Position.*;

class MagnetismEffectTest {

    private ChessBoard chessBoard;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        chessBoard.addEffect(new MagnetismEffect(chessBoard.at(c2).getPiece().get()));
    }

    @Test
    void should_not_be_able_to_move_pawn() {
        assertTrue(chessBoard.canMove(chessBoard.at(b2).getPiece().get(), b4));
        assertThrows(MagnetismException.class, () -> chessBoard.tryToMove(chessBoard.at(b2).getPiece().get(), b4));
        assertThrows(MagnetismException.class, () -> chessBoard.move(chessBoard.at(b2).getPiece().get(), b4));
    }

    @Test
    void should_not_be_able_to_move_knight() {
        assertTrue(chessBoard.canMove(chessBoard.at(b1).getPiece().get(), c3));
        assertThrows(MagnetismException.class, () -> chessBoard.tryToMove(chessBoard.at(b1).getPiece().get(), c3));
        assertThrows(MagnetismException.class, () -> chessBoard.move(chessBoard.at(b1).getPiece().get(), c3));
    }

    @Test
    void should_not_be_able_to_move_enemy_piece() {
        chessBoard.add(new Queen(Color.BLACK), c3);
        assertTrue(chessBoard.canMove(chessBoard.at(c3).getPiece().get(), h3));
        assertThrows(MagnetismException.class, () -> chessBoard.tryToMove(chessBoard.at(c3).getPiece().get(), h3));
        assertThrows(MagnetismException.class, () -> chessBoard.move(chessBoard.at(c3).getPiece().get(), h3));
    }

    @Test
    void should_not_be_able_to_move_enemy_piece_even_to_take_the_magnetic_piece() {
        Queen blackQueen = new Queen(Color.BLACK);
        chessBoard.add(blackQueen, c3);
        assertTrue(chessBoard.canMove(blackQueen, c2));
        assertThrows(MagnetismException.class, () -> chessBoard.tryToMove(blackQueen, c2));
        assertThrows(MagnetismException.class, () -> chessBoard.move(blackQueen, c2));
    }

    @Test
    void should_remove_effect_if_piece_moves() {
        chessBoard.move(chessBoard.at(c2).getPiece().get(), c3);

        assertTrue(chessBoard.getEffects().isEmpty());
        assertDoesNotThrow(() -> chessBoard.tryToMove(chessBoard.at(b2).getPiece().get(), b4));
        assertDoesNotThrow(() -> chessBoard.tryToMove(chessBoard.at(b1).getPiece().get(), c3));
    }

    @Test
    void should_remove_effect_if_piece_is_removed() {
        chessBoard.removePieceFromTheBoard(chessBoard.at(c2).getPiece().get());

        assertTrue(chessBoard.getEffects().isEmpty());
        assertDoesNotThrow(() -> chessBoard.tryToMove(chessBoard.at(b2).getPiece().get(), b4));
        assertDoesNotThrow(() -> chessBoard.tryToMove(chessBoard.at(b1).getPiece().get(), c3));
    }

    @Test
    void should_remove_effect_if_piece_is_taken() {
        Queen queen = new Queen(Color.BLACK);
        chessBoard.add(queen, c4);

        assertTrue(chessBoard.tryToMove(queen, c2));

        assertTrue(chessBoard.getEffects().isEmpty());
    }
}