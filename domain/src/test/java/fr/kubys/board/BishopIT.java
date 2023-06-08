package fr.kubys.board;

import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

public class BishopIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Bishop bishop = new Bishop(Color.WHITE);
        chessBoard.add(bishop, a1);
        Position squareToMoveOn = c3;
        assertTrue(chessBoard.emptyPath(bishop, squareToMoveOn));

        chessBoard.add(new Bishop(Color.BLACK), b2);

        assertFalse(chessBoard.emptyPath(bishop, squareToMoveOn));
    }

    @Test
    void should_move_on_cells_from_center() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        Set<Position> reachablePositions = Set.of(f3, d5, c6, b7, d3, f5, g6, h7);
        Bishop bishop = new Bishop(Color.WHITE);
        chessBoard.add(bishop, e4);

        assertEquals(reachablePositions, chessBoard.getAllAttackablePosition(bishop));
    }

    @Test
    void should_not_be_able_to_move() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();

        assertEquals(Collections.emptySet(), chessBoard.getAllAttackablePosition(chessBoard.at(f1).getPiece().get()));
    }
}
