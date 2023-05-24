package board.extra;

import board.ChessBoard;
import core.Color;
import org.junit.jupiter.api.Test;
import piece.Rock;
import piece.extra.BlackHole;

import static core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BlackHoleTest {

    @Test
    void should_not_be_movable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertFalse(chessBoard.tryToMove(blackHole, e5));
    }

    @Test
    void should_be_impregnable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertFalse(chessBoard.tryToMove(rock, e4));
        assertEquals(blackHole, chessBoard.at(e4).getPiece().get());
    }

    @Test
    void should_not_be_jumpable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertFalse(chessBoard.tryToMove(rock, e8));
        assertEquals(blackHole, chessBoard.at(e4).getPiece().get());
    }
}