import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                    .allMatch(pawn -> pawn instanceof Pawn && pawn.getColor() == Color.WHITE));

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
                    .allMatch(pawn -> pawn instanceof Pawn && pawn.getColor() == Color.BLACK));


            assertTrue(IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> IntStream.rangeClosed(3, 6).mapToObj(j -> BoardUtil.posToSquare(i, j)))
                    .flatMap(stringStream -> stringStream)
                    .allMatch(position -> chessBoard.at(position).getPiece().isEmpty())
            );
        }
    }
    
    @Nested
    class movePiece {
        @Test
        void move_() {
        }
    }
}