package fr.kubys.board;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import org.junit.jupiter.api.Test;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static fr.kubys.core.Position.*;

public class KnightIT {

    @Test
    void should_be_able_to_jump_over_pieces() {
        Knight knight = new Knight(Color.WHITE);
        new Knight(Color.BLACK);
        new Knight(Color.BLACK);
        new Knight(Color.BLACK);

        assertTrue(knight.squaresOnThePath(c2).isEmpty());
        assertTrue(knight.squaresOnThePath(b3).isEmpty());
    }

    @Test
    void should_move_on_cells_from_center() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        Set<Position> reachablePositions = Set.of(g5, g3, c3, c5, d6, f6);
        Knight knight = new Knight(Color.WHITE);
        chessBoard.add(knight, e4);

        assertEquals(reachablePositions, chessBoard.getAllAttackablePosition(knight));
    }

    @Test
    void should_move_on_cells_from_center_bis() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        Set<Position> reachablePositions = Set.of(g6, g4, f3, d3, c4, c6, d7, f7);
        Knight knight = new Knight(Color.WHITE);
        chessBoard.add(knight, e5);

        assertEquals(reachablePositions, chessBoard.getAllAttackablePosition(knight));
    }

    @Test
    void should_not_be_able_to_move() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        Set<Position> reachablePositions = Set.of(f3, h3);

        assertEquals(reachablePositions, chessBoard.getAllAttackablePosition(chessBoard.at(g1).getPiece().get()));
    }
}
