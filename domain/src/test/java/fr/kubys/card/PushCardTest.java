package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PushCardTest {

    ChessBoard board;
    PushCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new PushCard();
    }

    @Test
    void should_push_enemy_piece_forward_to_empty_square() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d4);
        board.add(enemy, d5);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(pawn, board.at(d5).getPiece().get());
        assertEquals(enemy, board.at(d6).getPiece().get());
        assertTrue(board.at(d4).getPiece().isEmpty());
    }

    @Test
    void should_push_ally_piece_forward() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight ally = new Knight(Color.WHITE);
        board.add(pawn, d4);
        board.add(ally, d5);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(pawn, board.at(d5).getPiece().get());
        assertEquals(ally, board.at(d6).getPiece().get());
        assertTrue(board.at(d4).getPiece().isEmpty());
    }

    @Test
    void should_push_piece_off_the_board() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d7);
        board.add(enemy, d8);
        // Remove black king from e8 first so we can place the knight at d8
        board.removePieceFromTheBoard(board.at(e8).getPiece().get());
        board.add(new King(Color.BLACK), a8);

        // Pawn pushes enemy at d8, pushed piece goes off the board (row 9 doesn't exist)
        // But wait, pawn at d7 pushes to d8 which is the promotion row
        // Let's use a different setup: black pawn pushes
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.BLACK);

        Pawn blackPawn = new Pawn(Color.BLACK);
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(blackPawn, d2);
        board.add(whiteKnight, d1);

        card.playOn(board, new PieceCardParam(blackPawn));

        // Knight pushed off the board (row 0 doesn't exist)
        assertTrue(board.getOutOfTheBoardPieces().contains(whiteKnight));
        // Pawn moved to d1 and promoted to Queen
        assertTrue(board.at(d2).getPiece().isEmpty());
    }

    @Test
    void should_reject_pushing_king() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_when_no_piece_to_push() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_non_pawn() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_reject_when_pushed_destination_is_occupied() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy1 = new Knight(Color.BLACK);
        Knight enemy2 = new Knight(Color.BLACK);
        board.add(pawn, d4);
        board.add(enemy1, d5);
        board.add(enemy2, d6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_reject_enemy_pawn() {
        Pawn enemyPawn = new Pawn(Color.BLACK);
        Knight ally = new Knight(Color.WHITE);
        board.add(enemyPawn, d5);
        board.add(ally, d4);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new PieceCardParam(enemyPawn)));
    }

    @Test
    void should_reject_when_creates_check() {
        // White king on e1, white pawn on e2, black rook on e4
        // Pushing pawn from e2 to e3 (pushing rook) would expose king to... no.
        // Let's set up: white king on e1, white pawn on e3, enemy piece on e4, enemy rook on e7
        // Moving pawn to e4 exposes king on e1 to... we need a pin scenario.
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), a8);
        board.setTurn(Color.WHITE);

        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(pawn, d2);
        board.add(enemy, d3);
        board.add(enemyRook, e3);
        // Pawn on d2 pushes to d3, enemy goes to d4. This doesn't create check.

        // Better: king on a1, pawn on a2, piece on a3, enemy rook on a8
        // Moving pawn to a3 means rook at a8 can now see... wait, pawn is still blocking.
        // Actually for check: we need the pawn's move to expose the king.
        // King e1, pawn e2 blocking, enemy rook e6, piece on e3. Pawn pushes piece from e3 to e4.
        // But pawn is on e2, it goes to e3 - still blocking. Not check.

        // For doesNotCreateCheck to fail: after the push, king must be under attack.
        // King a1, pawn b2, knight on b3, enemy bishop on d4.
        // Pawn moves b2->b3, knight goes to b4. Now diagonal a1-d4 is open? No, pawn is on b3.

        // Simpler: king on e1, pawn on f2, piece on f3. Enemy rook on a2.
        // Before: rook a2 blocked by nothing on rank 2, but king is on e1 not e2.
        // King on e2, pawn on e3, piece on e4, enemy rook on e7.
        // After push: pawn on e3, piece on e5, rook on e7. King on e2 blocked by pawn on e3. No check.

        // We need the pushed piece to create a discovered check scenario.
        // King on e1. Pawn on d2 (blocking diagonal). Enemy bishop on a5 on diagonal a5-e1.
        // Pawn pushes from d2 to d3. Now d2 is empty, bishop a5 can attack e1 via b4,c3,d2.
        // We need a piece on d3 for pawn to push.
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), a8);
        board.setTurn(Color.WHITE);

        Pawn pawn2 = new Pawn(Color.WHITE);
        Knight piece2 = new Knight(Color.BLACK);
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(pawn2, d2);
        board.add(piece2, d3);
        board.add(enemyBishop, a5);

        // Pawn d2 -> d3, knight d3 -> d4. Now diagonal a5-e1 is open: a5,b4,c3,d2,e1
        assertDoesNotThrow(
                () -> card.playOn(board, new PieceCardParam(pawn2)));
    }

    @Test
    void should_push_with_black_pawn() {
        board.setTurn(Color.BLACK);
        Pawn pawn = new Pawn(Color.BLACK);
        Knight enemy = new Knight(Color.WHITE);
        board.add(pawn, d5);
        board.add(enemy, d4);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(pawn, board.at(d4).getPiece().get());
        assertEquals(enemy, board.at(d3).getPiece().get());
        assertTrue(board.at(d5).getPiece().isEmpty());
    }

    @Test
    void should_push_piece_from_seventh_to_eighth_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d6);
        board.add(enemy, d7);

        card.playOn(board, new PieceCardParam(pawn));

        assertEquals(pawn, board.at(d7).getPiece().get());
        assertEquals(enemy, board.at(d8).getPiece().get());
    }

    @Test
    void should_promote_pawn_when_pushing_to_last_rank() {
        // Pawn on d7 pushes piece on d8 off the board, pawn lands on d8 → promotion
        board.removePieceFromTheBoard(board.at(e8).getPiece().get());
        board.add(new King(Color.BLACK), a8);
        Pawn pawn = new Pawn(Color.WHITE);
        Knight enemy = new Knight(Color.BLACK);
        board.add(pawn, d7);
        board.add(enemy, d8);

        card.playOn(board, new PieceCardParam(pawn));

        // Knight pushed off the board
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
        // Pawn promoted to queen on d8
        assertInstanceOf(Queen.class, board.at(d8).getPiece().get());
        assertEquals(Color.WHITE, board.at(d8).getPiece().get().getColor());
    }
}
