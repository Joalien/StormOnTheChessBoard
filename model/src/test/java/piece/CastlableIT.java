package piece;

import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static position.Position.*;

class CastlableIT {

    private ChessBoard chessBoard;
    private King whiteKing;
    private King blackKing;
    private Rock h1rock;
    private Rock a1rock;
    private Rock h8rock;
    private Rock a8rock;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        whiteKing = new King(Color.WHITE);
        chessBoard.add(whiteKing, e1);
        blackKing = new King(Color.BLACK);
        chessBoard.add(blackKing, e8);

        h1rock = new Rock(Color.WHITE);
        chessBoard.add(h1rock, h1);
        a1rock = new Rock(Color.WHITE);
        chessBoard.add(a1rock, a1);
        h8rock = new Rock(Color.BLACK);
        chessBoard.add(h8rock, h8);
        a8rock = new Rock(Color.BLACK);
        chessBoard.add(a8rock, a8);

        assertEquals(whiteKing, chessBoard.at(e1).getPiece().get());
        assertEquals(blackKing, chessBoard.at(e8).getPiece().get());
        assertEquals(h1rock, chessBoard.at(h1).getPiece().get());
        assertEquals(a1rock, chessBoard.at(a1).getPiece().get());
        assertEquals(h8rock, chessBoard.at(h8).getPiece().get());
        assertEquals(a8rock, chessBoard.at(a8).getPiece().get());
    }

    @Test
    void white_should_be_able_to_castle_king_side() {
        assertTrue(chessBoard.canMove(whiteKing, g1));

        assertTrue(chessBoard.tryToMove(whiteKing, g1));

        assertEquals(whiteKing, chessBoard.at(g1).getPiece().get());
        assertEquals(h1rock, chessBoard.at(f1).getPiece().get());
        assertTrue(chessBoard.at(e1).getPiece().isEmpty());
        assertTrue(chessBoard.at(h1).getPiece().isEmpty());
    }

    @Test
    void white_should_be_able_to_castle_queen_side() {
        assertTrue(chessBoard.canMove(whiteKing, c1));

        assertTrue(chessBoard.tryToMove(whiteKing, c1));

        assertEquals(whiteKing, chessBoard.at(c1).getPiece().get());
        assertEquals(a1rock, chessBoard.at(d1).getPiece().get());
        assertTrue(chessBoard.at(e1).getPiece().isEmpty());
        assertTrue(chessBoard.at(a1).getPiece().isEmpty());
    }

    @Test
    void black_should_be_able_to_castle_king_side() {
        assertTrue(chessBoard.canMove(blackKing, g8));

        assertTrue(chessBoard.tryToMove(blackKing, g8));

        assertEquals(blackKing, chessBoard.at(g8).getPiece().get());
        assertEquals(h8rock, chessBoard.at(f8).getPiece().get());
        assertTrue(chessBoard.at(e8).getPiece().isEmpty());
        assertTrue(chessBoard.at(h8).getPiece().isEmpty());
    }

    @Test
    void black_should_be_able_to_castle_queen_side() {
        assertTrue(chessBoard.canMove(blackKing, c8));

        assertTrue(chessBoard.tryToMove(blackKing, c8));

        assertEquals(blackKing, chessBoard.at(c8).getPiece().get());
        assertEquals(a8rock, chessBoard.at(d8).getPiece().get());
        assertTrue(chessBoard.at(e8).getPiece().isEmpty());
        assertTrue(chessBoard.at(a8).getPiece().isEmpty());
    }
}