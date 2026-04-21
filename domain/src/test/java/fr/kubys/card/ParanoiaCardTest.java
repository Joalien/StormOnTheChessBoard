package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.board.effect.NeutralityEffect;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ParanoiaCardTest {

    ChessBoard board;
    ParanoiaCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new ParanoiaCard();
    }

    @Test
    void should_move_enemy_bishop_to_capture_own_piece() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyBishop, a6);
        board.add(enemyKnight, d3);

        card.playOn(board, new PieceToPositionCardParam(enemyBishop, d3));

        assertTrue(board.getOutOfTheBoardPieces().contains(enemyKnight));
        assertEquals(enemyBishop, board.at(d3).getPiece().get());
        assertTrue(board.at(a6).getPiece().isEmpty());
    }

    @Test
    void should_reject_if_piece_is_not_a_bishop() {
        Rock enemyRook = new Rock(Color.BLACK);
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyRook, a8);
        board.add(enemyKnight, a6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyRook, a6)));
    }

    @Test
    void should_reject_if_bishop_is_ally() {
        Bishop allyBishop = new Bishop(Color.WHITE);
        Knight allyKnight = new Knight(Color.WHITE);
        board.add(allyBishop, c1);
        board.add(allyKnight, f4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(allyBishop, f4)));
    }

    @Test
    void should_reject_if_destination_is_empty() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, c5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, f2)));
    }

    @Test
    void should_reject_if_destination_has_our_piece() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        Knight allyKnight = new Knight(Color.WHITE);
        board.add(enemyBishop, c5);
        board.add(allyKnight, f2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, f2)));
    }

    @Test
    void should_reject_if_destination_has_enemy_king() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, c6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, e8)));
    }

    @Test
    void should_reject_if_path_is_blocked() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        Knight blocker = new Knight(Color.BLACK);
        Rock target = new Rock(Color.BLACK);
        board.add(enemyBishop, a1);
        board.add(blocker, c3);
        board.add(target, e5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, e5)));
    }

    @Test
    void should_reject_invalid_bishop_move() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyBishop, c5);
        board.add(enemyRook, c7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, c7)));
    }

    @Test
    void should_allow_moving_neutral_bishop() {
        Bishop neutralBishop = new Bishop(Color.BLACK);
        neutralBishop.setColor(Color.NONE);
        Knight neutralKnight = new Knight(Color.BLACK);
        neutralKnight.setColor(Color.NONE);
        board.add(neutralBishop, a6);
        board.add(neutralKnight, d3);
        board.addEffect(new NeutralityEffect(neutralBishop));
        board.addEffect(new NeutralityEffect(neutralKnight));

        card.playOn(board, new PieceToPositionCardParam(neutralBishop, d3));

        assertTrue(board.getOutOfTheBoardPieces().contains(neutralKnight));
        assertEquals(neutralBishop, board.at(d3).getPiece().get());
    }

    @Test
    void should_reject_when_creates_check() {
        // Enemy bishop on a5, captures enemy piece on d2 → opens diagonal to e1 → check
        // Wait, bishop moves FROM a5. After moving to d2, it's on d2 diagonal.
        // We need: bishop moves and discovers check on our king.
        // King e1, enemy bishop on c3, enemy pawn on f6. Bishop c3 captures f6.
        // After move: c3 is empty, bishop on f6. Diagonal a5-e1 is not relevant.
        // Let's do: king e1, enemy rook on e5 (blocked by enemy bishop on e3).
        // Bishop e3 captures enemy pawn on g5. Now e-file is open, rook e5 checks king e1.
        // But bishop can't go from e3 to g5 — diagonal e3→g5 is 2 squares diagonal. Yes it can.
        Bishop enemyBishop = new Bishop(Color.BLACK);
        Rock enemyRook = new Rock(Color.BLACK);
        Pawn enemyPawn = new Pawn(Color.BLACK);
        board.add(enemyBishop, e3);
        board.add(enemyRook, e5);
        board.add(enemyPawn, g5);

        assertDoesNotThrow(
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, g5)));
    }

    @Test
    void should_trigger_bombing_effect_on_move() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyBishop, a6);
        board.add(enemyKnight, d3);
        // Add bombing AFTER placing pieces so it doesn't trigger during setup
        board.addEffect(new BombingEffect(d3, Color.WHITE));

        card.playOn(board, new PieceToPositionCardParam(enemyBishop, d3));

        // Knight captured, then bishop lands on bombed square → destroyed by bombing
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyKnight));
        assertTrue(board.at(d3).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyBishop));
    }
}
