package fr.kubys.board.extra;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.core.Color;
import fr.kubys.piece.Rock;
import fr.kubys.piece.extra.BlackHoleSquare;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlackHoleSquareTest {

    @Test
    void should_not_be_movable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        chessBoard.setSquare(new BlackHoleSquare(e4));

        assertThrows(BlackHoleSquare.BlackHoleException.class, () -> chessBoard.tryToMove(chessBoard.at(e4).getPiece().get(), e5));
    }

    @Test
    void should_be_impregnable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        BlackHoleSquare blackHoleSquare = new BlackHoleSquare(e4);
        chessBoard.setSquare(blackHoleSquare);

        assertThrows(BlackHoleSquare.BlackHoleException.class, () -> chessBoard.tryToMove(rock, e4));
        assertEquals(blackHoleSquare, chessBoard.at(e4));
    }

    @Test
    void should_not_be_jumpable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        BlackHoleSquare blackHoleSquare = new BlackHoleSquare(e4);
        chessBoard.setSquare(blackHoleSquare);

        assertThrows(BlackHoleSquare.BlackHoleException.class, () -> chessBoard.tryToMove(rock, e8));
        assertEquals(blackHoleSquare, chessBoard.at(e4));
    }
}