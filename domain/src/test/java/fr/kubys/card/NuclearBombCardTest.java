package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class NuclearBombCardTest {

    ChessBoard board;
    NuclearBombCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        card = new NuclearBombCard();
    }

    @Test
    void should_explode_piece_and_adjacent() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, d5);
        Rock allyRock = new Rock(Color.WHITE);
        board.add(allyRock, e4);

        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyBishop));
        assertTrue(board.getOutOfTheBoardPieces().contains(allyRock));
        assertTrue(board.at(d4).getPiece().isEmpty());
        assertTrue(board.at(d5).getPiece().isEmpty());
        assertTrue(board.at(e4).getPiece().isEmpty());
    }

    @Test
    void should_not_affect_white_king() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b2);
        // White king on a1 is adjacent to b2

        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertFalse(board.getOutOfTheBoardPieces().contains(board.at(a1).getPiece().orElse(null)));
        assertTrue(board.at(a1).getPiece().isPresent());
    }

    @Test
    void both_kings_survive_explosion() {
        // Place both kings adjacent to the explosion
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), d3);
        board.add(new King(Color.BLACK), d5);
        board.setTurn(Color.WHITE);
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertTrue(board.at(d3).getPiece().isPresent(), "White king should survive");
        assertTrue(board.at(d5).getPiece().isPresent(), "Black king should survive");
    }

    @Test
    void should_reject_enemy_piece() {
        Rock enemyRock = new Rock(Color.BLACK);
        board.add(enemyRock, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(enemyRock)));
    }

    @Test
    void should_reject_king() {
        King king = (King) board.at(a1).getPiece().get();

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(king)));
    }

    @Test
    void should_allow_neutral_piece() {
        Knight neutral = new Knight(Color.BLACK);
        board.add(neutral, d4);
        neutral.setColor(Color.NONE);
        Bishop adjacent = new Bishop(Color.BLACK);
        board.add(adjacent, d5);

        card.playOn(board, new PieceCardParam(neutral));

        assertTrue(board.getOutOfTheBoardPieces().contains(neutral));
        assertTrue(board.getOutOfTheBoardPieces().contains(adjacent));
    }

    @Test
    void should_reject_if_last_move_captured() {
        // Simulate a capture: white knight takes black pawn
        BlackPawn pawn = new BlackPawn();
        board.add(pawn, d5);
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        board.tryToMove(knight, d5); // captures pawn

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_not_create_check_on_own_king() {
        // White king on a1, white knight on b1, black rook on h1
        // Exploding the knight would remove the shield and expose king to rook
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c1);
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyRook, h1);
        // Knight on c1: adjacent squares include b1, d1, c2, b2, d2
        // After explosion: knight gone, rook on h1 attacks king on a1 through empty b1..g1

        assertThrows(CheckException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }
}
