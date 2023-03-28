import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BishopIT {

    @Test
    @Disabled
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Bishop bishop = new Bishop("a1", Color.WHITE);
        chessBoard.add(bishop, "a1");
        String squareToMoveOn = "c3";
        Assertions.assertTrue(bishop.nothingOnThePath(squareToMoveOn));

        chessBoard.add(new Bishop("b2", Color.BLACK), "b2");

        Assertions.assertFalse(bishop.nothingOnThePath(squareToMoveOn));
    }
}
