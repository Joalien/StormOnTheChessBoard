package fr.kubys.board;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.BlackHole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import fr.kubys.core.Position;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static fr.kubys.core.Position.*;

class ChessBoardTest {

    @Test
    void play_a_sicilian_game() {
        ChessBoard cb = ChessBoard.createWithInitialState();
        assertDoesNotThrow(() -> cb.tryToMove(e2, e4));
        assertDoesNotThrow(() -> cb.tryToMove(c7, c5));
        assertDoesNotThrow(() -> cb.tryToMove(g1, f3));
        assertDoesNotThrow(() -> cb.tryToMove(d7, d6));
        assertDoesNotThrow(() -> cb.tryToMove(d2, d4));
        assertDoesNotThrow(() -> cb.tryToMove(c5, d4));
        assertDoesNotThrow(() -> cb.tryToMove(f3, d4));
        assertDoesNotThrow(() -> cb.tryToMove(g8, f6));
        assertDoesNotThrow(() -> cb.tryToMove(c1, e3));
        assertDoesNotThrow(() -> cb.tryToMove(g7, g6));
        assertDoesNotThrow(() -> cb.tryToMove(b1, c3));
        assertDoesNotThrow(() -> cb.tryToMove(f8, g7));
        assertDoesNotThrow(() -> cb.tryToMove(d1, d2));
        assertDoesNotThrow(() -> cb.tryToMove(e8, g8));
        assertDoesNotThrow(() -> cb.tryToMove(e1, c1));

        assertTrue(cb.at(e4).getPiece().get() instanceof WhitePawn);
        assertTrue(cb.at(e8).getPiece().isEmpty());
        assertTrue(cb.at(f8).getPiece().get() instanceof Rock);
        assertTrue(cb.at(g8).getPiece().get() instanceof King);
        assertTrue(cb.at(h8).getPiece().isEmpty());
        assertTrue(cb.at(e1).getPiece().isEmpty());
        assertTrue(cb.at(d1).getPiece().get() instanceof Rock);
        assertTrue(cb.at(c1).getPiece().get() instanceof King);
        assertTrue(cb.at(b1).getPiece().isEmpty());
        assertTrue(cb.at(a1).getPiece().isEmpty());

        assertEquals(2, cb.getOutOfTheBoardPieces().size());
    }

    @Test
    void should_not_be_able_to_add_two_pieces_on_the_same_square() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Executable addQueenOnE4 = () -> chessBoard.add(new Queen(Color.WHITE), e4);

        assertDoesNotThrow(addQueenOnE4);
        assertThrows(IllegalArgumentException.class, addQueenOnE4);
    }

    @Nested
    class CreateBoard {
        @Test
        void create_empty_chessboard() {
            ChessBoard chessBoard = ChessBoard.createEmpty();

            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> IntStream.rangeClosed(1, 8).mapToObj(j -> Position.posToSquare(i, j)))
                    .flatMap(stringStream -> stringStream)
                    .allMatch(position -> chessBoard.at(position).getPiece().isEmpty()));
        }

        @Test
        void create_initial_chessboard() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();

            // White pieces
            assertTrue(chessBoard.at(a1).getPiece().get() instanceof Rock);
            assertEquals(Color.WHITE, chessBoard.at(a1).getPiece().get().getColor());
            assertTrue(chessBoard.at(b1).getPiece().get() instanceof Knight);
            assertEquals(Color.WHITE, chessBoard.at(b1).getPiece().get().getColor());
            assertTrue(chessBoard.at(c1).getPiece().get() instanceof Bishop);
            assertEquals(Color.WHITE, chessBoard.at(c1).getPiece().get().getColor());
            assertTrue(chessBoard.at(d1).getPiece().get() instanceof Queen);
            assertEquals(Color.WHITE, chessBoard.at(d1).getPiece().get().getColor());
            assertTrue(chessBoard.at(e1).getPiece().get() instanceof King);
            assertEquals(Color.WHITE, chessBoard.at(e1).getPiece().get().getColor());
            assertTrue(chessBoard.at(f1).getPiece().get() instanceof Bishop);
            assertEquals(Color.WHITE, chessBoard.at(f1).getPiece().get().getColor());
            assertTrue(chessBoard.at(g1).getPiece().get() instanceof Knight);
            assertEquals(Color.WHITE, chessBoard.at(g1).getPiece().get().getColor());
            assertTrue(chessBoard.at(h1).getPiece().get() instanceof Rock);
            assertEquals(Color.WHITE, chessBoard.at(h1).getPiece().get().getColor());
            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> Position.posToSquare(i, 2))
                    .map(position -> chessBoard.at(position).getPiece().get())
                    .allMatch(pawn -> pawn instanceof WhitePawn));

            // Black pieces
            assertTrue(chessBoard.at(a8).getPiece().get() instanceof Rock);
            assertEquals(Color.BLACK, chessBoard.at(a8).getPiece().get().getColor());
            assertTrue(chessBoard.at(b8).getPiece().get() instanceof Knight);
            assertEquals(Color.BLACK, chessBoard.at(b8).getPiece().get().getColor());
            assertTrue(chessBoard.at(c8).getPiece().get() instanceof Bishop);
            assertEquals(Color.BLACK, chessBoard.at(c8).getPiece().get().getColor());
            assertTrue(chessBoard.at(d8).getPiece().get() instanceof Queen);
            assertEquals(Color.BLACK, chessBoard.at(d8).getPiece().get().getColor());
            assertTrue(chessBoard.at(e8).getPiece().get() instanceof King);
            assertEquals(Color.BLACK, chessBoard.at(e8).getPiece().get().getColor());
            assertTrue(chessBoard.at(f8).getPiece().get() instanceof Bishop);
            assertEquals(Color.BLACK, chessBoard.at(f8).getPiece().get().getColor());
            assertTrue(chessBoard.at(g8).getPiece().get() instanceof Knight);
            assertEquals(Color.BLACK, chessBoard.at(g8).getPiece().get().getColor());
            assertTrue(chessBoard.at(h8).getPiece().get() instanceof Rock);
            assertEquals(Color.BLACK, chessBoard.at(h8).getPiece().get().getColor());
            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> Position.posToSquare(i, 7))
                    .map(position -> chessBoard.at(position).getPiece().get())
                    .allMatch(pawn -> pawn instanceof BlackPawn));


            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> IntStream.rangeClosed(3, 6).mapToObj(j -> Position.posToSquare(i, j)))
                    .flatMap(stringStream -> stringStream)
                    .allMatch(position -> chessBoard.at(position).getPiece().isEmpty())
            );
        }
    }

    @Nested
    class MovePieces {
        @Test
        void move_bishop_on_empty_chessboard() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, a1);
            assertEquals(bishop, chessBoard.at(a1).getPiece().get());
            assertTrue(chessBoard.at(h8).getPiece().isEmpty());

            assertDoesNotThrow(() -> chessBoard.tryToMove(bishop, h8));

            assertTrue(chessBoard.at(a1).getPiece().isEmpty());
            assertEquals(bishop, chessBoard.at(h8).getPiece().get());
        }

        @Test
        void move_knight_to_f3_on_initial_chessboard() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();
            Piece knight = chessBoard.at(g1).getPiece().get();
            assertTrue(chessBoard.at(f3).getPiece().isEmpty());

            assertDoesNotThrow(() -> chessBoard.tryToMove(chessBoard.at(g1).getPiece().get(), f3));

            assertTrue(chessBoard.at(g1).getPiece().isEmpty());
            assertEquals(knight, chessBoard.at(f3).getPiece().get());
        }

        @Test
        void should_not_move_on_itself() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(queen, e4);

            assertThrows(IllegalArgumentException.class, () -> chessBoard.tryToMove(queen, e4));
        }
    }

    @Nested
    class TakePiece {
        @Test
        void take_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Bishop bishop = new Bishop(Color.WHITE);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(bishop, a1);
            chessBoard.add(queen, h8);
            assertEquals(bishop, chessBoard.at(a1).getPiece().get());
            assertEquals(queen, chessBoard.at(h8).getPiece().get());

            assertDoesNotThrow(() -> chessBoard.tryToMove(bishop, h8));

            assertTrue(chessBoard.at(a1).getPiece().isEmpty());
            assertEquals(bishop, chessBoard.at(h8).getPiece().get());
            assertNull(queen.getPosition());
            assertTrue(chessBoard.getOutOfTheBoardPieces().contains(queen));
        }
    }

    @Nested
    class ValidMove {

        @Test
        void should_not_be_able_to_move_on_ally_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, a1);
            chessBoard.add(new Queen(Color.WHITE), h8);

            assertFalse(chessBoard.isEnemyOrEmpty(bishop, h8));
            assertThrows(IllegalMoveException.class, () -> chessBoard.tryToMove(bishop, h8));
        }

        @Test
        void should_be_able_to_move_on_enemy_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, a1);
            chessBoard.add(new Queen(Color.BLACK), h8);

            assertTrue(chessBoard.isEnemyOrEmpty(bishop, h8));
            assertDoesNotThrow(() -> chessBoard.tryToMove(bishop, h8));
        }

        @Test
        void should_not_be_able_to_move_king_in_check() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            King king = new King(Color.WHITE);
            chessBoard.add(king, a1);
            assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(king, a2));

            chessBoard.add(new Queen(Color.BLACK), b3);

            assertTrue(chessBoard.doesMovingPieceCheckOurOwnKing(king, a2));
        }

        @Test
        void should_not_be_able_to_move_king_in_check_bis() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Set<Position> validMoves = Set.of(e5, d3, f4);
            Set<Position> invalidMoves = Set.of(d5, f3);
            King king = new King(Color.WHITE);
            chessBoard.add(king, e4);
            validMoves.forEach(pos -> assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(king, pos)));
            invalidMoves.forEach(pos -> assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(king, pos)));

            chessBoard.add(new Bishop(Color.BLACK), a8);

            validMoves.forEach(pos -> assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(king, pos)));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.doesMovingPieceCheckOurOwnKing(king, pos)));
        }

        @Test
        void should_not_be_able_to_move_a_pinned_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            King king = new King(Color.BLACK);
            chessBoard.add(king, e8);
            Knight knight = new Knight(Color.BLACK);
            chessBoard.add(knight, e5);

            chessBoard.add(new Rock(Color.WHITE), e1);

            assertTrue(Position.generateAllPositions().stream()
                    .filter(knight::isPositionTheoreticallyReachable)
                    .allMatch(pos -> chessBoard.doesMovingPieceCheckOurOwnKing(knight, pos)));
        }

        @Test
        void should_be_able_to_move_a_pinned_piece_if_it_still_block_the_chess() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Set<Position> validMoves = Set.of(b2, c3, d4, f6, g7, h8);
            Set<Position> invalidMoves = Set.of(d6, h2);
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, e5);
            chessBoard.add(new King(Color.WHITE), a1);
            validMoves.forEach(pos -> assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(bishop, pos)));
            invalidMoves.forEach(pos -> assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(bishop, pos)));

            chessBoard.add(new Bishop(Color.BLACK), h8);

            validMoves.forEach(pos -> assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(bishop, pos)));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.doesMovingPieceCheckOurOwnKing(bishop, pos)));
        }

        @Test
        void should_be_able_to_move_to_prevent_check() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            chessBoard.add(new King(Color.BLACK), e8);
            chessBoard.add(new Rock(Color.WHITE), e1);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, c4);
            Set<Position> validMoves = Set.of(e6, e4, e2);
            Set<Position> invalidMoves = Position.generateAllPositions()
                    .stream()
                    .filter(s -> !validMoves.contains(s))
                    .filter(queen::isPositionTheoreticallyReachable)
                    .collect(Collectors.toSet());


            validMoves.forEach(pos -> assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(queen, pos)));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.doesMovingPieceCheckOurOwnKing(queen, pos)));
        }

        @Test
        void should_be_able_to_take_the_piece_that_make_chess() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            chessBoard.add(new King(Color.BLACK), e8);
            chessBoard.add(new Rock(Color.WHITE), e4);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, h1);
            Set<Position> invalidMoves = Position.generateAllPositions()
                    .stream()
                    .filter(s -> !s.equals(e4))
                    .filter(queen::isPositionTheoreticallyReachable)
                    .collect(Collectors.toSet());


            assertFalse(chessBoard.doesMovingPieceCheckOurOwnKing(queen, e4));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.doesMovingPieceCheckOurOwnKing(queen, pos)));
        }
    }
}