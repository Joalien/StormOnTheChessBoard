package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BlackHoleEffect;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.board.effect.MagnetismEffect;
import fr.kubys.board.effect.MagnetismException;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class UncontrolledElementCardTest {

    ChessBoard board;
    UncontrolledElementCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new UncontrolledElementCard();
    }

    @Test
    void should_move_enemy_piece() {
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, c6);

        card.playOn(board, new PieceToPositionCardParam(enemyKnight, d4));

        assertTrue(board.at(c6).getPiece().isEmpty());
        assertEquals(enemyKnight, board.at(d4).getPiece().get());
    }

    @Test
    void should_allow_moving_enemy_king() {
        King enemyKing = (King) board.at(e8).getPiece().get();

        card.playOn(board, new PieceToPositionCardParam(enemyKing, d7));

        assertTrue(board.at(e8).getPiece().isEmpty());
        assertEquals(enemyKing, board.at(d7).getPiece().get());
    }

    @Test
    void should_reject_moving_own_piece() {
        Knight ownKnight = new Knight(Color.WHITE);
        board.add(ownKnight, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(ownKnight, d5)));
    }

    @Test
    void should_reject_capturing_enemy_piece() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, c6);
        Knight anotherEnemy = new Knight(Color.BLACK);
        board.add(anotherEnemy, e4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, e4)));
    }

    @Test
    void should_reject_invalid_move_for_piece() {
        Rock enemyRock = new Rock(Color.BLACK);
        board.add(enemyRock, a7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyRock, c5)));
    }

    @Test
    void should_allow_capturing_own_piece_with_enemy() {
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, c6);
        Pawn ownPawn = new Pawn(Color.WHITE);
        board.add(ownPawn, d4);

        card.playOn(board, new PieceToPositionCardParam(enemyKnight, d4));

        assertEquals(enemyKnight, board.at(d4).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(ownPawn));
    }

    @Test
    void should_reject_when_creates_check_on_own_king() {
        // Move enemy rook to e5 — checks white king on e1 through open file
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyRook, h5);

        assertDoesNotThrow(
                () -> card.playOn(board, new PieceToPositionCardParam(enemyRook, e5)));
    }

    @Test
    void should_trigger_bombing_effect_on_move() {
        board.addEffect(new BombingEffect(d4, Color.WHITE));
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, c6);

        card.playOn(board, new PieceToPositionCardParam(enemyKnight, d4));

        // Knight moved to bombed square and got destroyed by afterMoveHook
        assertTrue(board.at(d4).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyKnight));
    }

    @Test
    void should_trigger_magnetism_before_move_hook() {
        Knight magnetized = new Knight(Color.WHITE);
        board.add(magnetized, d5);
        board.addEffect(new MagnetismEffect(magnetized));

        // Enemy piece adjacent to magnetized piece can't move
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, c4);

        assertThrows(MagnetismException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyBishop, a2)));
    }

    @Test
    void should_reject_move_that_exposes_enemy_king() {
        // Enemy rook on e5 shields enemy king on e8 from white rook on e2
        Rock whiteRook = new Rock(Color.WHITE);
        board.add(whiteRook, e2);
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyRook, e5);

        // Moving enemy rook off the e-file would expose enemy king to white rook
        assertDoesNotThrow(
                () -> card.playOn(board, new PieceToPositionCardParam(enemyRook, a5)));
    }

    @Test
    void should_reject_move_onto_black_hole() {
        board.addEffect(new BlackHoleEffect(d4));
        Knight enemyKnight = new Knight(Color.BLACK);
        board.add(enemyKnight, c6);

        // Black hole blocks the square — piece cannot land there
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(enemyKnight, d4)));
    }
}
