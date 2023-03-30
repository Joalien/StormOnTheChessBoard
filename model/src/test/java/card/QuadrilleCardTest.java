package card;

import board.ChessBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Rock;

class QuadrilleCardTest {

    private ChessBoard chessBoard;
    private Rock a1Rock;
    private Rock h1Rock;
    private Rock a8Rock;
    private Rock h8Rock;
    private SCCard quadrilleCard;


    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        a1Rock = (Rock) chessBoard.at("a1").getPiece().get();
        h1Rock = (Rock) chessBoard.at("h1").getPiece().get();
        a8Rock = (Rock) chessBoard.at("a8").getPiece().get();
        h8Rock = (Rock) chessBoard.at("h8").getPiece().get();
        quadrilleCard = new QuadrilleCard(QuadrilleCard.Direction.CLOCKWISE);
    }

    @Nested
    class Success {
        @Test
        void should_turn_counterclockwise() {
            Assertions.assertTrue(new QuadrilleCard(QuadrilleCard.Direction.COUNTERCLOCKWISE).playOn(chessBoard));

            Assertions.assertEquals(a1Rock, chessBoard.at("h1").getPiece().get());
            Assertions.assertEquals(h1Rock, chessBoard.at("h8").getPiece().get());
            Assertions.assertEquals(h8Rock, chessBoard.at("a8").getPiece().get());
            Assertions.assertEquals(a8Rock, chessBoard.at("a1").getPiece().get());
        }
    }

    @Nested
    class Failure {

    }
}