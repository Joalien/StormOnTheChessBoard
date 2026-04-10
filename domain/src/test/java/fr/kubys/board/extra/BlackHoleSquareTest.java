package fr.kubys.board.extra;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.board.effect.BlackHoleEffect;
import fr.kubys.core.Color;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlackHoleSquareTest {

    @Test
    void should_not_be_movable_to() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        chessBoard.addEffect(new BlackHoleEffect(e4));
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);

        assertThrows(IllegalMoveException.class, () -> chessBoard.tryToMove(rock, e4));
    }

    @Test
    void should_be_impregnable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        chessBoard.addEffect(new BlackHoleEffect(e4));

        assertThrows(IllegalMoveException.class, () -> chessBoard.tryToMove(rock, e4));
    }

    @Test
    void should_not_be_jumpable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e1);
        chessBoard.addEffect(new BlackHoleEffect(e4));

        assertThrows(IllegalMoveException.class, () -> chessBoard.tryToMove(rock, e8));
    }
}
