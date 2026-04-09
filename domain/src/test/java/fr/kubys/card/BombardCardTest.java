package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BombardCardTest {

    ChessBoard board;
    Rock rock;
    BombardCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        rock = new Rock(Color.WHITE);
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(rock, a1);
        board.setTurn(Color.WHITE);
        card = new BombardCard();
    }

    @Nested
    class Success {
        @Test
        void should_capture_enemy_immediately_after_piece_to_jump_over_on_file() {
            board.add(new WhitePawn(), a3);
            Knight enemy = new Knight(Color.BLACK);
            board.add(enemy, a4);

            assertDoesNotThrow(() -> card.playOn(board, new PieceToPositionCardParam(rock, a4)));

            assertEquals(rock, board.at(a4).getPiece().get());
            assertTrue(board.at(a1).getPiece().isEmpty());
            assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
        }

        @Test
        void should_capture_enemy_immediately_after_piece_to_jump_over_on_row() {
            board.add(new Bishop(Color.WHITE), c1);
            Knight enemy = new Knight(Color.BLACK);
            board.add(enemy, d1);

            assertDoesNotThrow(() -> card.playOn(board, new PieceToPositionCardParam(rock, d1)));

            assertEquals(rock, board.at(d1).getPiece().get());
            assertTrue(board.at(a1).getPiece().isEmpty());
        }

        @Test
        void should_jump_over_enemy_piece() {
            board.add(new Knight(Color.BLACK), a5);
            Queen enemy = new Queen(Color.BLACK);
            board.add(enemy, a6);

            assertDoesNotThrow(() -> card.playOn(board, new PieceToPositionCardParam(rock, a6)));

            assertEquals(rock, board.at(a6).getPiece().get());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_reject_non_rook_piece() {
            Bishop bishop = new Bishop(Color.WHITE);
            board.add(bishop, c1);
            board.add(new WhitePawn(), c3);
            board.add(new Knight(Color.BLACK), c4);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(bishop, c4)));
        }

        @Test
        void should_reject_enemy_rook() {
            Rock enemyRock = new Rock(Color.BLACK);
            board.add(enemyRock, h8);
            board.add(new WhitePawn(), h5);
            board.add(new Knight(Color.WHITE), h4);

            assertThrows(CannotMoveThisColorException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(enemyRock, h4)));
        }

        @Test
        void should_reject_if_target_not_immediately_after_piece_to_jump_over() {
            board.add(new WhitePawn(), a3);
            board.add(new Knight(Color.BLACK), a6);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(rock, a6)));
        }

        @Test
        void should_reject_if_target_is_empty() {
            board.add(new WhitePawn(), a3);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(rock, a4)));
        }

        @Test
        void should_reject_if_target_is_ally() {
            board.add(new WhitePawn(), a3);
            board.add(new WhitePawn(), a4);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(rock, a4)));
        }

        @Test
        void should_reject_if_no_piece_to_jump_over() {
            board.add(new Knight(Color.BLACK), a4);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(rock, a4)));
        }

        @Test
        void should_reject_if_two_pieces_to_jump_over() {
            board.add(new WhitePawn(), a3);
            board.add(new WhitePawn(), a5);
            board.add(new Knight(Color.BLACK), a6);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(rock, a6)));
        }

        @Test
        void should_reject_diagonal_move() {
            board.add(new Knight(Color.BLACK), d4);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(rock, d4)));
        }

        @Test
        void should_reject_if_creates_check() {
            board = ChessBoard.createEmpty();
            rock = new Rock(Color.WHITE);
            board.add(new King(Color.WHITE), d1);
            board.add(new King(Color.BLACK), e8);
            board.add(rock, a1);
            board.add(new Queen(Color.BLACK), h1);
            board.add(new WhitePawn(), a5);
            board.add(new Knight(Color.BLACK), a6);
            board.setTurn(Color.WHITE);

            // Rook leaves row 1 → king exposed to black queen
            assertThrows(Exception.class,
                    () -> card.playOn(board, new PieceToPositionCardParam(rock, a6)));
        }
    }
}
