import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BishopIT {

    @Test
    @Disabled
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Bishop bishop = new Bishop(1, 1, true, 'B');
        chessBoard.add(bishop, "a1");
        String squareToMoveOn = "c3";
        Assertions.assertTrue(bishop.nothingOnThePath(squareToMoveOn));

        chessBoard.add(new Bishop(2, 2, false, 'b'), "b2");

        Assertions.assertFalse(bishop.nothingOnThePath(squareToMoveOn));
    }
}
