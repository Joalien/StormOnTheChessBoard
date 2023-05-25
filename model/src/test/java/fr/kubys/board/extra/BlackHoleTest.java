package fr.kubys.board.extra;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.core.Color;
import org.junit.jupiter.api.Test;
import fr.kubys.piece.Rock;
import fr.kubys.piece.extra.BlackHole;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BlackHoleTest {

    @Test
    void should_not_be_movable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertThrows(BlackHole.BlackHoleException.class, () -> chessBoard.tryToMove(blackHole, e5));
    }

    @Test
    void should_be_impregnable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertThrows(BlackHole.BlackHoleException.class, () -> chessBoard.tryToMove(rock, e4));
        assertEquals(blackHole, chessBoard.at(e4).getPiece().get());
    }

    @Test
    void should_not_be_jumpable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertThrows(IllegalMoveException.class, () -> chessBoard.tryToMove(rock, e8));
        assertEquals(blackHole, chessBoard.at(e4).getPiece().get());
    }
}