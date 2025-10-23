package fr.kubys.card;

import fr.kubys.board.CannotTakeKingException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class HomeCardTest {

    @Nested
    class Success {
        @Test
        void should_go_back_on_starting_square() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, e4);
            HomeCard homeCard = new HomeCard();
            chessBoard.setTurn(Color.WHITE);

            assertDoesNotThrow(() -> homeCard.playOn(chessBoard, new PieceToPositionCardParam(knight, g1)));

            assertEquals(knight, chessBoard.at(g1).getPiece().get());
            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
        }

        @Test
        void should_go_back_on_starting_square_and_take_enemy_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, e4);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, g1);
            HomeCard homeCard = new HomeCard();
            chessBoard.setTurn(Color.WHITE);

            assertDoesNotThrow(() -> homeCard.playOn(chessBoard, new PieceToPositionCardParam(knight, g1)));

            assertEquals(knight, chessBoard.at(g1).getPiece().get());
            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
            assertNull(queen.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().contains(queen));
        }
    }

    @Nested
    class Failure {

        @Test
        void should_not_be_able_to_rollback_a_pawn() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new WhitePawn();
            chessBoard.add(pawn, e4);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, g1);
            HomeCard homeCard = new HomeCard();
            chessBoard.setTurn(Color.WHITE);

            assertThrows(IllegalArgumentException.class, () -> homeCard.playOn(chessBoard, new PieceToPositionCardParam(pawn, g1)));

            assertEquals(pawn, chessBoard.at(e4).getPiece().get());
            assertEquals(queen, chessBoard.at(g1).getPiece().get());
            assertEquals(g1, queen.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_starting_square_if_ally_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, e4);
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, g1);
            HomeCard homeCard = new HomeCard();
            chessBoard.setTurn(Color.WHITE);

            assertThrows(IllegalArgumentException.class, () -> homeCard.playOn(chessBoard, new PieceToPositionCardParam(knight, g1)));

            assertEquals(knight, chessBoard.at(e4).getPiece().get());
            assertEquals(queen, chessBoard.at(g1).getPiece().get());
            assertEquals(g1, queen.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_none_starting_square() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, e4);
            HomeCard homeCard = new HomeCard();
            chessBoard.setTurn(Color.BLACK);

            assertThrows(IllegalArgumentException.class, () -> homeCard.playOn(chessBoard, new PieceToPositionCardParam(queen, g1)));

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
            assertTrue(chessBoard.at(g1).getPiece().isEmpty());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_starting_square_of_enemy_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, e4);
            HomeCard homeCard = new HomeCard();
            chessBoard.setTurn(Color.WHITE);

            assertThrows(CannotMoveThisColorException.class, () -> homeCard.playOn(chessBoard, new PieceToPositionCardParam(queen, d1)));

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
            assertTrue(chessBoard.at(d1).getPiece().isEmpty());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_starting_square_if_enemy_king() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            chessBoard.add(knight, e4);
            King king = new King(Color.BLACK);
            chessBoard.add(king, g1);
            HomeCard homeCard = new HomeCard();
            chessBoard.setTurn(Color.WHITE);

            assertThrows(CannotTakeKingException.class, () -> homeCard.playOn(chessBoard, new PieceToPositionCardParam(knight, g1)));

            assertEquals(knight, chessBoard.at(e4).getPiece().get());
            assertEquals(king, chessBoard.at(g1).getPiece().get());
            assertEquals(g1, king.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        @Disabled
        void should_not_create_check() {
            // TODO
        }
    }
}