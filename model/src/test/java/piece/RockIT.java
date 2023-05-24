package piece;

import board.ChessBoard;
import org.junit.jupiter.api.Test;
import position.Position;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static position.Position.*;

public class RockIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, a1);
        Position squareHidden = h1;
        Position squareNotHidden = a8;
        assertTrue(chessBoard.emptyPath(rock, squareHidden));
        assertTrue(chessBoard.emptyPath(rock, squareNotHidden));

        chessBoard.add(new Rock(Color.BLACK), e1);

        assertFalse(chessBoard.emptyPath(rock, squareHidden));
        assertTrue(chessBoard.emptyPath(rock, squareNotHidden));
    }

    @Test
    void should_move_on_cells_from_center() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        Set<Position> reachablePositions = Set.of(e3, e5, e6, e7, a4, b4, c4, d4, f4, g4, h4);
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, e4);

        assertEquals(reachablePositions, chessBoard.getAllAttackablePosition(rock));
    }

    @Test
    void should_not_be_able_to_move() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();

        assertEquals(Collections.emptySet(), chessBoard.getAllAttackablePosition(chessBoard.at(h1).getPiece().get()));
    }
}
