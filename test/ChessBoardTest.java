import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ChessBoardTest {

    @Nested
    class CreateBoard {
        @Test
        void create_empty_chessboard() {
            ChessBoard chessBoard = ChessBoard.createEmpty();

            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> IntStream.rangeClosed(1, 8).mapToObj(j -> BoardUtil.posToSquare(i, j)))
                    .flatMap(stringStream -> stringStream)
                    .allMatch(position -> chessBoard.at(position).getPiece().isEmpty()));
        }

        @Test
        void create_initial_chessboard() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();

            // White pieces
            assertTrue(chessBoard.at("a1").getPiece().get() instanceof Rock);
            assertEquals(Color.WHITE, chessBoard.at("a1").getPiece().get().getColor());
            assertTrue(chessBoard.at("b1").getPiece().get() instanceof Knight);
            assertEquals(Color.WHITE, chessBoard.at("b1").getPiece().get().getColor());
            assertTrue(chessBoard.at("c1").getPiece().get() instanceof Bishop);
            assertEquals(Color.WHITE, chessBoard.at("c1").getPiece().get().getColor());
            assertTrue(chessBoard.at("d1").getPiece().get() instanceof Queen);
            assertEquals(Color.WHITE, chessBoard.at("d1").getPiece().get().getColor());
            assertTrue(chessBoard.at("e1").getPiece().get() instanceof King);
            assertEquals(Color.WHITE, chessBoard.at("e1").getPiece().get().getColor());
            assertTrue(chessBoard.at("f1").getPiece().get() instanceof Bishop);
            assertEquals(Color.WHITE, chessBoard.at("f1").getPiece().get().getColor());
            assertTrue(chessBoard.at("g1").getPiece().get() instanceof Knight);
            assertEquals(Color.WHITE, chessBoard.at("g1").getPiece().get().getColor());
            assertTrue(chessBoard.at("h1").getPiece().get() instanceof Rock);
            assertEquals(Color.WHITE, chessBoard.at("h1").getPiece().get().getColor());
            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> BoardUtil.posToSquare(i, 2))
                    .map(position -> chessBoard.at(position).getPiece().get())
                    .allMatch(pawn -> pawn instanceof WhitePawn));

            // Black pieces
            assertTrue(chessBoard.at("a8").getPiece().get() instanceof Rock);
            assertEquals(Color.BLACK, chessBoard.at("a8").getPiece().get().getColor());
            assertTrue(chessBoard.at("b8").getPiece().get() instanceof Knight);
            assertEquals(Color.BLACK, chessBoard.at("b8").getPiece().get().getColor());
            assertTrue(chessBoard.at("c8").getPiece().get() instanceof Bishop);
            assertEquals(Color.BLACK, chessBoard.at("c8").getPiece().get().getColor());
            assertTrue(chessBoard.at("d8").getPiece().get() instanceof Queen);
            assertEquals(Color.BLACK, chessBoard.at("d8").getPiece().get().getColor());
            assertTrue(chessBoard.at("e8").getPiece().get() instanceof King);
            assertEquals(Color.BLACK, chessBoard.at("e8").getPiece().get().getColor());
            assertTrue(chessBoard.at("f8").getPiece().get() instanceof Bishop);
            assertEquals(Color.BLACK, chessBoard.at("f8").getPiece().get().getColor());
            assertTrue(chessBoard.at("g8").getPiece().get() instanceof Knight);
            assertEquals(Color.BLACK, chessBoard.at("g8").getPiece().get().getColor());
            assertTrue(chessBoard.at("h8").getPiece().get() instanceof Rock);
            assertEquals(Color.BLACK, chessBoard.at("h8").getPiece().get().getColor());
            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> BoardUtil.posToSquare(i, 7))
                    .map(position -> chessBoard.at(position).getPiece().get())
                    .allMatch(pawn -> pawn instanceof BlackPawn));


            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> IntStream.rangeClosed(3, 6).mapToObj(j -> BoardUtil.posToSquare(i, j)))
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
            String a1 = "a1";
            String h8 = "h8";
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, a1);
            assertEquals(bishop, chessBoard.at(a1).getPiece().get());
            assertTrue(chessBoard.at(h8).getPiece().isEmpty());

            assertTrue(chessBoard.tryToMove(bishop, h8));

            assertTrue(chessBoard.at(a1).getPiece().isEmpty());
            assertEquals(bishop, chessBoard.at(h8).getPiece().get());
        }

        @Test
        void move_knight_to_f3_on_initial_chessboard() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();
            String g1 = "g1";
            String f3 = "f3";
            Piece knight = chessBoard.at(g1).getPiece().get();
            assertTrue(chessBoard.at(f3).getPiece().isEmpty());

            assertTrue(chessBoard.tryToMove(chessBoard.at(g1).getPiece().get(), f3));

            assertTrue(chessBoard.at(g1).getPiece().isEmpty());
            assertEquals(knight, chessBoard.at(f3).getPiece().get());
        }

        @Test
        void should_not_move_on_itself() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e4 = "e4";
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
            String a1 = "a1";
            String h8 = "h8";
            Bishop bishop = new Bishop(Color.WHITE);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(bishop, a1);
            chessBoard.add(queen, h8);
            assertEquals(bishop, chessBoard.at(a1).getPiece().get());
            assertEquals(queen, chessBoard.at(h8).getPiece().get());

            assertTrue(chessBoard.tryToMove(bishop, h8));

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
            String a1 = "a1";
            String h8 = "h8";
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, a1);
            chessBoard.add(new Queen(Color.WHITE), h8);

            assertTrue(chessBoard.isAllyOnPosition(bishop, h8));
            assertFalse(chessBoard.tryToMove(bishop, h8));
        }

        @Test
        void should_be_able_to_move_on_enemy_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String a1 = "a1";
            String h8 = "h8";
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, a1);
            chessBoard.add(new Queen(Color.BLACK), h8);

            assertFalse(chessBoard.isAllyOnPosition(bishop, h8));
            assertTrue(chessBoard.tryToMove(bishop, h8));
        }

        @Test
        void should_not_be_able_to_move_king_in_check() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String a1 = "a1";
            String a2 = "a2";
            String b3 = "b3";
            King king = new King(Color.WHITE);
            chessBoard.add(king, a1);
            assertFalse(chessBoard.createCheck(king, a2));

            chessBoard.add(new Queen(Color.BLACK), b3);

            assertTrue(chessBoard.createCheck(king, a2));
        }

        @Test
        void should_not_be_able_to_move_king_in_check_bis() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e4 = "e4";
            Set<String> validMoves = Set.of("e5", "d3", "f4");
            Set<String> invalidMoves = Set.of("d5", "f3");
            String a8 = "a8";
            King king = new King(Color.WHITE);
            chessBoard.add(king, e4);
            validMoves.forEach(pos -> assertFalse(chessBoard.createCheck(king, pos)));
            invalidMoves.forEach(pos -> assertFalse(chessBoard.createCheck(king, pos)));

            chessBoard.add(new Bishop(Color.BLACK), a8);

            validMoves.forEach(pos -> assertFalse(chessBoard.createCheck(king, pos)));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.createCheck(king, pos)));
        }

        @Test
        void should_not_be_able_to_move_a_pinned_piece() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e8 = "e8";
            String e5 = "e5";
            String e1 = "e1";
            King king = new King(Color.BLACK);
            chessBoard.add(king, e8);
            Knight knight = new Knight(Color.BLACK);
            chessBoard.add(knight, e5);
//            assertTrue(BoardUtil.generateAllPositions().stream()
//                    .filter(knight::reachableSquares)
//                    .noneMatch(pos -> chessBoard.createCheck(knight, pos)));

            chessBoard.add(new Rock(Color.WHITE), e1);

            assertTrue(BoardUtil.generateAllPositions().stream()
                    .filter(knight::reachableSquares)
                    .peek(pos -> System.out.println(pos + " " + chessBoard.createCheck(knight, pos)))
                    .allMatch(pos -> chessBoard.createCheck(knight, pos)));
        }

        @Test
        void should_not_be_able_to_move_a_pinned_piece_bis () {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e5 = "e5";
            String h8 = "h8";
            String a1 = "a1";
            Set<String> validMoves = Set.of("b2", "c3", "d4", "f6", "g7");
            Set<String> invalidMoves = Set.of("d6", "h2");
            Bishop bishop = new Bishop(Color.WHITE);
            chessBoard.add(bishop, e5);
            chessBoard.add(new King(Color.WHITE), a1);
            validMoves.forEach(pos -> assertFalse(chessBoard.createCheck(bishop, pos)));
            invalidMoves.forEach(pos -> assertFalse(chessBoard.createCheck(bishop, pos)));

            chessBoard.add(new Bishop(Color.BLACK), h8);

            validMoves.forEach(pos -> assertFalse(chessBoard.createCheck(bishop, pos)));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.createCheck(bishop, pos)));
        }

        @Test
        void should_be_able_to_move_to_prevent_check() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e8 = "e8";
            String c4 = "c4";
            String e1 = "e1";
            chessBoard.add(new King(Color.BLACK), e8);
            chessBoard.add(new Rock(Color.WHITE), e1);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, c4);
            Set<String> validMoves = Set.of("e6", "e4", "e2");
            Set<String> invalidMoves = BoardUtil.generateAllPositions()
                    .stream()
                    .filter(s -> !validMoves.contains(s))
                    .filter(queen::reachableSquares)
                    .collect(Collectors.toSet());


            validMoves.forEach(pos -> assertFalse(chessBoard.createCheck(queen, pos)));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.createCheck(queen, pos)));
        }

        @Test
        void should_be_able_to_take_the_piece_that_make_chess() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            String e8 = "e8";
            String e4 = "e4";
            String h1 = "h1";
            chessBoard.add(new King(Color.BLACK), e8);
            chessBoard.add(new Rock(Color.WHITE), e4);
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, h1);
            Set<String> invalidMoves = BoardUtil.generateAllPositions()
                    .stream()
                    .filter(s -> !s.equals(e4))
                    .filter(queen::reachableSquares)
                    .collect(Collectors.toSet());


            assertFalse(chessBoard.createCheck(queen, e4));
            invalidMoves.forEach(pos -> assertTrue(chessBoard.createCheck(queen, pos)));
        }
    }

    @Test
    void play_a_sicilian_game() {
        ChessBoard cb = ChessBoard.createWithInitialState();
        assertTrue(cb.tryToMove("e2", "e4"));
        assertTrue(cb.tryToMove("c7", "c5"));
        assertTrue(cb.tryToMove("g1", "f3"));
        assertTrue(cb.tryToMove("d7", "d6"));
        assertTrue(cb.tryToMove("d2", "d4"));
        assertTrue(cb.tryToMove("c5", "d4"));
        assertTrue(cb.tryToMove("f3", "d4"));
        assertTrue(cb.tryToMove("g8", "f6"));
        assertTrue(cb.tryToMove("c1", "e3"));
        assertTrue(cb.tryToMove("g7", "g6"));
        assertTrue(cb.tryToMove("b1", "c3"));
        assertTrue(cb.tryToMove("f8", "g7"));
        assertTrue(cb.tryToMove("d1", "d2"));
        assertTrue(cb.tryToMove("e8", "g8"));
        assertTrue(cb.tryToMove("e1", "c1"));

        assertTrue(cb.at("e4").getPiece().get() instanceof WhitePawn);
        assertTrue(cb.at("e8").getPiece().isEmpty());
        assertTrue(cb.at("f8").getPiece().get() instanceof Rock);
        assertTrue(cb.at("g8").getPiece().get() instanceof King);
        assertTrue(cb.at("h8").getPiece().isEmpty());
        assertTrue(cb.at("e1").getPiece().isEmpty());
        assertTrue(cb.at("d1").getPiece().get() instanceof Rock);
        assertTrue(cb.at("c1").getPiece().get() instanceof King);
        assertTrue(cb.at("b1").getPiece().isEmpty());
        assertTrue(cb.at("a1").getPiece().isEmpty());

        assertEquals(2, cb.getOutOfTheBoardPieces().size());
    }
}