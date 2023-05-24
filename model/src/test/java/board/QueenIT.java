package board;

import board.ChessBoard;
import core.Color;
import org.junit.jupiter.api.Test;
import core.Position;
import piece.Queen;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static core.Position.*;

public class QueenIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Queen queen = new Queen(Color.WHITE);
        chessBoard.add(queen, a1);
        Position squareHidden = h1;
        Position squareHidden_bis = h8;
        Position squareNotHidden = a8;
        assertTrue(chessBoard.emptyPath(queen, squareHidden));
        assertTrue(chessBoard.emptyPath(queen, squareHidden_bis));
        assertTrue(chessBoard.emptyPath(queen, squareNotHidden));

        chessBoard.add(new Queen(Color.BLACK), e1);
        chessBoard.add(new Queen(Color.BLACK), d4);

        assertFalse(chessBoard.emptyPath(queen, squareHidden));
        assertFalse(chessBoard.emptyPath(queen, squareHidden_bis));
        assertTrue(chessBoard.emptyPath(queen, squareNotHidden));
    }

    @Test
    void should_move_on_cells_from_center() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        Set<Position> reachableBishopPositions = Set.of(f3, d5, c6, b7, d3, f5, g6, h7);
        Set<Position> reachableRockPositions = Set.of(e3, e5, e6, e7, a4, b4, c4, d4, f4, g4, h4);
        Set<Position> reachablePositions = Stream.concat(reachableBishopPositions.stream(), reachableRockPositions.stream()).collect(Collectors.toSet());

        Queen queen = new Queen(Color.WHITE);
        chessBoard.add(queen, e4);

        assertEquals(reachablePositions, chessBoard.getAllAttackablePosition(queen));
    }

    @Test
    void should_not_be_able_to_move() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();

        assertEquals(Collections.emptySet(), chessBoard.getAllAttackablePosition(chessBoard.at(d1).getPiece().get()));
    }
}
