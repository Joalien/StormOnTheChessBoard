package card;

import board.ChessBoard;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HomeCardTest {

    @Nested
    class Success {
        @Test
        void should_go_back_on_starting_square() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            String e4 = "e4";
            String g1 = "g1";
            chessBoard.add(knight, e4);
            Card homeCard = new HomeCard();
            homeCard.setIsPlayedBy(Color.WHITE);

            assertTrue(homeCard.playOn(chessBoard, List.of(knight, g1)));

            assertEquals(knight, chessBoard.at(g1).getPiece().get());
            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
        }

        @Test
        void should_go_back_on_starting_square_and_take_enemy_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            String e4 = "e4";
            String g1 = "g1";
            chessBoard.add(knight, e4);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, g1);
            Card homeCard = new HomeCard();
            homeCard.setIsPlayedBy(Color.WHITE);

            assertTrue(homeCard.playOn(chessBoard, List.of(knight, g1)));

            assertEquals(knight, chessBoard.at(g1).getPiece().get());
            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
            assertTrue(queen.getSquare().isEmpty());
            assertTrue(chessBoard.getOutOfTheBoardPieces().contains(queen));
        }
    }

    @Nested
    class Failure {

        @Test
        void should_not_be_able_to_rollback_a_pawn() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new WhitePawn();
            String e4 = "e4";
            String g1 = "g1";
            chessBoard.add(pawn, e4);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, g1);
            Card homeCard = new HomeCard();
            homeCard.setIsPlayedBy(Color.WHITE);

            assertThrows(IllegalArgumentException.class, () -> homeCard.playOn(chessBoard, List.of(pawn, g1)));

            assertEquals(pawn, chessBoard.at(e4).getPiece().get());
            assertEquals(queen, chessBoard.at(g1).getPiece().get());
            assertEquals(g1, queen.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_starting_square_if_ally_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            String e4 = "e4";
            String g1 = "g1";
            chessBoard.add(knight, e4);
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, g1);
            Card homeCard = new HomeCard();
            homeCard.setIsPlayedBy(Color.WHITE);

            assertThrows(IllegalArgumentException.class, () -> homeCard.playOn(chessBoard, List.of(knight, g1)));

            assertEquals(knight, chessBoard.at(e4).getPiece().get());
            assertEquals(queen, chessBoard.at(g1).getPiece().get());
            assertEquals(g1, queen.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_none_starting_square() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Queen queen = new Queen(Color.BLACK);
            String e4 = "e4";
            String g1 = "g1";
            chessBoard.add(queen, e4);
            Card homeCard = new HomeCard();
            homeCard.setIsPlayedBy(Color.BLACK);

            assertThrows(IllegalArgumentException.class, () -> homeCard.playOn(chessBoard, List.of(queen, g1)));

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
            assertTrue(chessBoard.at(g1).getPiece().isEmpty());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_starting_square_of_enemy_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Queen queen = new Queen(Color.BLACK);
            String e4 = "e4";
            String d1 = "d1";
            chessBoard.add(queen, e4);
            Card homeCard = new HomeCard();
            homeCard.setIsPlayedBy(Color.WHITE);

            assertThrows(IllegalColorException.class, () -> homeCard.playOn(chessBoard, List.of(queen, d1)));

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
            assertTrue(chessBoard.at(d1).getPiece().isEmpty());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        void should_not_go_back_on_starting_square_if_enemy_king() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Knight knight = new Knight(Color.WHITE);
            String e4 = "e4";
            String g1 = "g1";
            chessBoard.add(knight, e4);
            King king = new King(Color.BLACK);
            chessBoard.add(king, g1);
            Card homeCard = new HomeCard();
            homeCard.setIsPlayedBy(Color.WHITE);

            assertThrows(IllegalStateException.class, () -> homeCard.playOn(chessBoard, List.of(knight, g1)));

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