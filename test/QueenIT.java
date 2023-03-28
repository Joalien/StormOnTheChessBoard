import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueenIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Queen queen = new Queen(Color.WHITE);
        chessBoard.add(queen, "a1");
        String squareHidden = "h1";
        String squareHidden_bis = "h8";
        String squareNotHidden = "a8";
        assertTrue(chessBoard.emptyPath(queen, squareHidden));
        assertTrue(chessBoard.emptyPath(queen, squareHidden_bis));
        assertTrue(chessBoard.emptyPath(queen, squareNotHidden));

        chessBoard.add(new Queen(Color.BLACK), "e1");
        chessBoard.add(new Queen(Color.BLACK), "d4");

        assertFalse(chessBoard.emptyPath(queen, squareHidden));
        assertFalse(chessBoard.emptyPath(queen, squareHidden_bis));
        assertTrue(chessBoard.emptyPath(queen, squareNotHidden));
    }
}
