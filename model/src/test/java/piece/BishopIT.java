package piece;

import board.ChessBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BishopIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Bishop bishop = new Bishop(Color.WHITE);
        chessBoard.add(bishop, "a1");
        String squareToMoveOn = "c3";
        assertTrue(chessBoard.emptyPath(bishop, squareToMoveOn));

        chessBoard.add(new Bishop(Color.BLACK), "b2");

        assertFalse(chessBoard.emptyPath(bishop, squareToMoveOn));
    }
}
