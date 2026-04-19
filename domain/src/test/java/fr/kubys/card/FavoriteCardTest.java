package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FavoriteCardTest {

    ChessBoard board;
    FavoriteCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new FavoriteCard();
    }

    @Test
    void should_move_knight_like_queen_diagonally() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        card.playOn(board, new PieceToPositionCardParam(knight, f6));

        assertEquals(knight, board.at(f6).getPiece().get());
        assertTrue(board.at(c3).getPiece().isEmpty());
    }

    @Test
    void should_move_knight_like_queen_straight() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        card.playOn(board, new PieceToPositionCardParam(knight, c6));

        assertEquals(knight, board.at(c6).getPiece().get());
    }

    @Test
    void should_reject_pawn() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(pawn, c4)));
    }

    @Test
    void should_reject_capture() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, f6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, f6)));
    }

    @Test
    void should_reject_non_queen_move() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, d5))); // L-shape, not queen
    }

    @Test
    void should_reject_blocked_path() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a3);
        Pawn blocker = new Pawn(Color.WHITE);
        board.add(blocker, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, f3)));
    }

    @Test
    void should_trigger_bombing_effect_on_move() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        board.addEffect(new BombingEffect(f6, Color.BLACK));

        card.playOn(board, new PieceToPositionCardParam(knight, f6));

        // Knight moved to bombed square and got destroyed by afterMoveHook
        assertTrue(board.at(f6).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
    }
}
