package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class AstralTravelCardTest {

    ChessBoard board;
    AstralTravelCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new AstralTravelCard();
    }

    @Test
    void should_remove_piece_temporarily() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.at(d4).getPiece().isEmpty());
    }

    @Test
    void should_restore_piece_after_move() {
        Pawn blocker = new Pawn(Color.BLACK);
        board.add(blocker, d4);

        card.playOn(board, new PieceCardParam(blocker));
        assertTrue(board.at(d4).getPiece().isEmpty());

        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, a2);
        board.move(whitePawn, a3);

        assertTrue(board.at(d4).getPiece().isPresent());
        assertEquals(blocker, board.at(d4).getPiece().get());
    }

    @Test
    void should_allow_removing_king() {
        card.playOn(board, new PieceCardParam(board.at(e1).getPiece().get()));

        assertTrue(board.at(e1).getPiece().isEmpty());
    }

    @Test
    void should_not_allow_piece_to_land_on_removed_piece_square() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, d1);

        card.playOn(board, new PieceCardParam(knight));

        assertThrows(RuntimeException.class, () -> board.tryToMove(rock, d4));
    }

    @Test
    void should_allow_piece_to_pass_through_removed_piece_square() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, d1);

        card.playOn(board, new PieceCardParam(knight));

        board.move(rock, d7);
        assertEquals(rock, board.at(d7).getPiece().get());
        assertEquals(knight, board.at(d4).getPiece().get());
    }

    @Test
    void should_allow_piece_to_pass_through_removed_piece_square_with_tryToMove() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, d1);

        card.playOn(board, new PieceCardParam(knight));

        board.tryToMove(rock, d7);
        assertEquals(rock, board.at(d7).getPiece().get());
        assertEquals(knight, board.at(d4).getPiece().get());
    }

    @Test
    void should_remove_own_piece() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);

        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.at(d4).getPiece().isEmpty());
    }

    @Test
    void should_remove_enemy_piece() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, d4);

        card.playOn(board, new PieceCardParam(enemyBishop));

        assertTrue(board.at(d4).getPiece().isEmpty());
    }

    @Test
    void should_restore_enemy_piece_after_move() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, d4);

        card.playOn(board, new PieceCardParam(enemyBishop));

        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, a2);
        board.move(whitePawn, a3);

        assertTrue(board.at(d4).getPiece().isPresent());
        assertEquals(enemyBishop, board.at(d4).getPiece().get());
    }

    @Test
    void should_not_allow_enemy_piece_to_land_on_removed_piece_square() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, d4);
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, d1);

        card.playOn(board, new PieceCardParam(enemyBishop));

        assertThrows(RuntimeException.class, () -> board.tryToMove(rock, d4));
    }

    @Test
    void should_remove_pawn() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);

        card.playOn(board, new PieceCardParam(pawn));

        assertTrue(board.at(d2).getPiece().isEmpty());
    }

    @Test
    void should_restore_pawn_after_move() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);

        card.playOn(board, new PieceCardParam(pawn));

        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);
        board.move(knight, c3);

        assertTrue(board.at(d2).getPiece().isPresent());
        assertEquals(pawn, board.at(d2).getPiece().get());
    }

    @Test
    void should_allow_rook_to_pass_through_removed_enemy_piece_square() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, d4);
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, d1);

        card.playOn(board, new PieceCardParam(enemyBishop));

        board.move(rock, d7);
        assertEquals(rock, board.at(d7).getPiece().get());
        assertEquals(enemyBishop, board.at(d4).getPiece().get());
    }

    @Test
    void should_allow_rook_to_pass_through_removed_enemy_piece_square_with_tryToMove() {
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, d4);
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, d1);

        card.playOn(board, new PieceCardParam(enemyBishop));

        board.tryToMove(rock, d7);
        assertEquals(rock, board.at(d7).getPiece().get());
        assertEquals(enemyBishop, board.at(d4).getPiece().get());
    }
}
