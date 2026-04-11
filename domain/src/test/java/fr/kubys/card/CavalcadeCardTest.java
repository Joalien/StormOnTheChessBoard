package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CavalcadeCardTest {

    private CavalcadeCard cavalcadeCard;
    private ChessBoard chessBoard;

    @BeforeEach
    void setUp() {
        cavalcadeCard = new CavalcadeCard();
        chessBoard = ChessBoard.createEmpty();
        chessBoard.setTurn(Color.WHITE);
        // Kings are required for check detection
        chessBoard.add(new King(Color.WHITE), e1);
        chessBoard.add(new King(Color.BLACK), h8);
    }

    @Nested
    class Success {

        @Test
        void should_move_queen_like_a_knight() {
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, d4);

            assertDoesNotThrow(() -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(queen, e6)));

            assertEquals(queen, chessBoard.at(e6).getPiece().get());
            assertTrue(chessBoard.at(d4).getPiece().isEmpty());
        }

        @Test
        void should_move_bishop_like_a_knight() {
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, c3);

            assertDoesNotThrow(() -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(bishop, d5)));

            assertEquals(bishop, chessBoard.at(d5).getPiece().get());
            assertTrue(chessBoard.at(c3).getPiece().isEmpty());
        }

        @Test
        void should_move_rook_like_a_knight() {
            Rock rook = new Rock(Color.WHITE);
            chessBoard.add(rook, a1);

            assertDoesNotThrow(() -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(rook, b3)));

            assertEquals(rook, chessBoard.at(b3).getPiece().get());
            assertTrue(chessBoard.at(a1).getPiece().isEmpty());
        }

        @Test
        void should_move_pawn_like_a_knight() {
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e2);

            assertDoesNotThrow(() -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(pawn, f4)));

            assertEquals(pawn, chessBoard.at(f4).getPiece().get());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void should_jump_over_pieces() {
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, d4);
            chessBoard.add(new Bishop(Color.WHITE), d5);
            chessBoard.add(new Rock(Color.WHITE), e5);

            assertDoesNotThrow(() -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(queen, e6)));

            assertEquals(queen, chessBoard.at(e6).getPiece().get());
        }
    }

    @Nested
    class Failure {

        @Test
        void should_not_move_if_not_a_knight_move() {
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, d4);

            assertThrows(IllegalArgumentException.class,
                    () -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(queen, d6)));

            assertEquals(queen, chessBoard.at(d4).getPiece().get());
        }

        @Test
        void should_not_capture_enemy_piece() {
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, d4);
            Knight enemy = new Knight(Color.BLACK);
            chessBoard.add(enemy, e6);

            assertThrows(IllegalArgumentException.class,
                    () -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(queen, e6)));

            assertEquals(queen, chessBoard.at(d4).getPiece().get());
            assertEquals(enemy, chessBoard.at(e6).getPiece().get());
        }

        @Test
        void should_not_move_to_ally_occupied_square() {
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, d4);
            Bishop ally = new Bishop(Color.WHITE);
            chessBoard.add(ally, e6);

            assertThrows(IllegalArgumentException.class,
                    () -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(queen, e6)));

            assertEquals(queen, chessBoard.at(d4).getPiece().get());
            assertEquals(ally, chessBoard.at(e6).getPiece().get());
        }

        @Test
        void should_not_move_enemy_piece() {
            Knight enemy = new Knight(Color.BLACK);
            chessBoard.add(enemy, d4);

            assertThrows(CannotMoveThisColorException.class,
                    () -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(enemy, e6)));

            assertEquals(enemy, chessBoard.at(d4).getPiece().get());
        }

        @Test
        void should_not_create_check() {
            Rock rook = new Rock(Color.WHITE);
            chessBoard.add(rook, e4); // rook protects king on e1 from e8 black queen
            Queen blackQueen = new Queen(Color.BLACK);
            chessBoard.add(blackQueen, e8);

            // Moving rook away from e-file would expose king to the queen
            assertThrows(CheckException.class,
                    () -> cavalcadeCard.playOn(chessBoard, new PieceToPositionCardParam(rook, f6)));

            assertEquals(rook, chessBoard.at(e4).getPiece().get());
        }
    }
}
