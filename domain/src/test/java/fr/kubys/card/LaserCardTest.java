package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class LaserCardTest {

    ChessBoard board;
    LaserCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new LaserCard();
    }

    @Test
    void should_destroy_pieces_along_ray() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn enemy1 = new Pawn(Color.BLACK);
        board.add(enemy1, a3);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(enemy2, a5);

        card.playOn(board, new PieceToPositionCardParam(rock, a8));

        // Rock stays, enemies destroyed
        assertEquals(rock, board.at(a1).getPiece().orElse(null));
        assertTrue(board.at(a3).getPiece().isEmpty());
        assertTrue(board.at(a5).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy1));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy2));
    }

    @Test
    void should_destroy_ally_pieces_too() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn ally = new Pawn(Color.WHITE);
        board.add(ally, a4);

        card.playOn(board, new PieceToPositionCardParam(rock, a8));

        assertTrue(board.at(a4).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(ally));
    }

    @Test
    void should_not_destroy_kings() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, a5);
        // King on e8 is not in the way. Let's put a piece behind a king
        // Put black king on a6, enemy pawn on a7
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        King blackKing = new King(Color.BLACK);
        board.add(blackKing, a6);
        board.add(rock, a1);
        Pawn pawnBehindKing = new Pawn(Color.BLACK);
        board.add(pawnBehindKing, a7);
        board.setTurn(Color.WHITE);

        card.playOn(board, new PieceToPositionCardParam(rock, a3));

        // King survives, pawn behind king is destroyed
        assertTrue(board.at(a6).getPiece().isPresent());
        assertTrue(board.at(a7).getPiece().isEmpty());
    }

    @Test
    void should_reject_knight() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, c3)));
    }

    @Test
    void should_reject_enemy_piece() {
        Rock rock = new Rock(Color.BLACK);
        board.add(rock, a4);

        assertThrows(RuntimeException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a7)));
    }

    @Test
    void should_work_with_bishop_diagonal() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, a1);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, c3);

        card.playOn(board, new PieceToPositionCardParam(bishop, b2));

        assertTrue(board.at(c3).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_reject_unreachable_direction() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);

        // b2 is diagonal - not reachable by rook
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, b2)));
    }
}
