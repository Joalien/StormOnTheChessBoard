package card;

import piece.extra.BlackHole;
import board.ChessBoard;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Queen;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackHoleCardTest {

    @Nested
    class Success {
        @Test
        void should_not_be_able_to_add_piece_on_black_hole() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e4 = "e4";
            SCCard blackHoleCard = new BlackHoleCard(e4);

            assertTrue(blackHoleCard.playOn(chessBoard));

            assertTrue(chessBoard.at(e4).getPiece().get() instanceof BlackHole);
            assertThrows(IllegalArgumentException.class, () -> chessBoard.add(new Queen(Color.WHITE), e4));
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throw_if_square_is_not_empty() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e4 = "e4";
            chessBoard.add(new Queen(Color.WHITE), e4);
            SCCard blackHoleCard = new BlackHoleCard(e4);

            assertThrows(IllegalArgumentException.class, () -> blackHoleCard.playOn(chessBoard));
        }

        @Test
        void should_throw_if_square_already_have_blackHole() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            SCCard blackHoleCard = new BlackHoleCard("e4");

            assertTrue(blackHoleCard.playOn(chessBoard));
            assertThrows(IllegalArgumentException.class, () -> blackHoleCard.playOn(chessBoard));
        }

        @Test
        void should_throw_if_square_is_not_in_chessboard() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            SCCard blackHoleCard = new BlackHoleCard("h9");

            assertThrows(IllegalArgumentException.class, () -> blackHoleCard.playOn(chessBoard));

        }
    }
}