import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            HomeCard homeCard = new HomeCard(knight, g1);

            assertTrue(chessBoard.play(homeCard));

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
            HomeCard homeCard = new HomeCard(knight, g1);

            assertTrue(chessBoard.play(homeCard));

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
            HomeCard homeCard = new HomeCard(pawn, g1);

            assertThrows(IllegalArgumentException.class, () -> chessBoard.play(homeCard));

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
            HomeCard homeCard = new HomeCard(knight, g1);

            assertThrows(IllegalArgumentException.class, () -> chessBoard.play(homeCard));

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
            HomeCard homeCard = new HomeCard(queen, g1);

            assertThrows(IllegalArgumentException.class, () -> chessBoard.play(homeCard));

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
            assertTrue(chessBoard.at(g1).getPiece().isEmpty());
            assertTrue(chessBoard.getOutOfTheBoardPieces().isEmpty());
        }

        @Test
        @Disabled
        void should_not_create_check() {
            // TODO
        }
    }
}