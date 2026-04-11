package fr.kubys.board;

import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

public class PawnIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Pawn pawn = new Pawn(Color.WHITE);
        chessBoard.add(pawn, e2);

        chessBoard.tryToMove(pawn, e4);

        assertEquals(pawn, chessBoard.at(e4).getPiece().get());
    }

    @Nested
    class Movements {
        @Test
        void should_move_white_pawn_on_empty_cell() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e2);

            assertTrue(chessBoard.canMove(pawn, e3));
            assertTrue(chessBoard.canMove(pawn, e4));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, d3));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, f3));

            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e1));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, f1));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, d1));
        }

        @Test
        void should_not_move_white_pawn_on_white_cell() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e2);
            chessBoard.add(new Queen(Color.WHITE), e3);
            chessBoard.add(new Queen(Color.WHITE), e4);
            chessBoard.add(new Queen(Color.WHITE), d3);
            chessBoard.add(new Queen(Color.WHITE), f3);
            chessBoard.add(new Queen(Color.WHITE), e1);
            chessBoard.add(new Queen(Color.WHITE), f1);
            chessBoard.add(new Queen(Color.WHITE), d1);

            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e3));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e4));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, d3));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, f3));

            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e1));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, f1));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, d1));
        }

        @Test
        void should_move_white_pawn_on_black_cell() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e2);
            chessBoard.add(new Queen(Color.BLACK), e3);
            chessBoard.add(new Queen(Color.BLACK), e4);
            chessBoard.add(new Queen(Color.BLACK), d3);
            chessBoard.add(new Queen(Color.BLACK), f3);
            chessBoard.add(new Queen(Color.BLACK), e1);
            chessBoard.add(new Queen(Color.BLACK), f1);
            chessBoard.add(new Queen(Color.BLACK), d1);

            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e3));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e4));
            assertTrue(chessBoard.canMove(pawn, d3));
            assertTrue(chessBoard.canMove(pawn, f3));

            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e1));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, f1));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, d1));
        }

        @Test
        void should_move_black_pawn() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.BLACK);
            chessBoard.add(pawn, e2);

            chessBoard.add(new Queen(Color.WHITE), f1);
            chessBoard.add(new Queen(Color.WHITE), d1);

            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e3));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e4));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, d3));
            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, f3));

            assertTrue(chessBoard.canMove(pawn, e1));
            assertTrue(chessBoard.canMove(pawn, f1));
            assertTrue(chessBoard.canMove(pawn, d1));
        }

        @Test
        void should_move_black_pawn_on_4_positions() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.BLACK);
            chessBoard.add(pawn, e7);
            chessBoard.add(new Queen(Color.WHITE), f6);
            chessBoard.add(new Queen(Color.WHITE), d6);
            Set<Position> reachablePositions = Set.of(f6, d6, e6, e5);

            assertEquals(reachablePositions, chessBoard.getAllAttackablePosition(pawn));
        }

        @Test
        void should_not_be_able_to_move_on_itself() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e5);

            assertThrows(IllegalMoveException.class, () -> chessBoard.canMove(pawn, e5));
        }
    }

    @Nested
    class Promotion {

        @Test
        void white_pawn_should_promote_to_queen_on_row_8() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e7);

            chessBoard.tryToMove(pawn, e8);

            assertInstanceOf(Queen.class, chessBoard.at(e8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(e8).getPiece().get().getColor());
            assertTrue(chessBoard.at(e7).getPiece().isEmpty());
        }

        @Test
        void black_pawn_should_promote_to_queen_on_row_1() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.BLACK);
            chessBoard.add(pawn, e2);

            chessBoard.tryToMove(pawn, e1);

            assertInstanceOf(Queen.class, chessBoard.at(e1).getPiece().get());
            assertEquals(Color.BLACK, chessBoard.at(e1).getPiece().get().getColor());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void white_pawn_should_promote_when_capturing_on_row_8() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e7);
            Knight enemy = new Knight(Color.BLACK);
            chessBoard.add(enemy, d8);

            chessBoard.tryToMove(pawn, d8);

            assertInstanceOf(Queen.class, chessBoard.at(d8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(d8).getPiece().get().getColor());
            assertTrue(chessBoard.getOutOfTheBoardPieces().contains(enemy));
        }

        @Test
        void white_pawn_should_not_promote_before_row_8() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e6);

            chessBoard.tryToMove(pawn, e7);

            assertInstanceOf(Pawn.class, chessBoard.at(e7).getPiece().get());
        }
    }
}
