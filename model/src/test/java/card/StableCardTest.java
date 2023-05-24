package card;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Knight;
import piece.Rock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static core.Position.*;

class StableCardTest {

    private Rock rock;
    private Knight knight;
    private Card stableCard;
    private ChessBoard chessBoard;


    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        rock = (Rock) chessBoard.at(h1).getPiece().get();
        knight = (Knight) chessBoard.at(g1).getPiece().get();
        stableCard = new StableCard();
    }

    @Nested
    class Success {
        @Test
        void should_swap_pieces() {
            assertTrue(stableCard.playOn(chessBoard, List.of(rock, knight)));

            assertEquals(rock, chessBoard.at(g1).getPiece().get());
            assertEquals(knight, chessBoard.at(h1).getPiece().get());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_not_swap_pieces_of_different_color() {
            Knight blackNight = (Knight) chessBoard.at(g8).getPiece().get();
            stableCard = new StableCard();

            assertThrows(IllegalArgumentException.class, () -> stableCard.playOn(chessBoard, List.of(rock, blackNight)));

            assertEquals(rock, chessBoard.at(h1).getPiece().get());
            assertEquals(blackNight, chessBoard.at(g8).getPiece().get());
        }
    }
}