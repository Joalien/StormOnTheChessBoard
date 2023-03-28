import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RockIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, "a1");
        String squareHidden = "h1";
        String squareNotHidden = "a8";
        assertTrue(chessBoard.emptyPath(rock, squareHidden));
        assertTrue(chessBoard.emptyPath(rock, squareNotHidden));

        chessBoard.add(new Rock(Color.BLACK), "e1");

        assertFalse(chessBoard.emptyPath(rock, squareHidden));
        assertTrue(chessBoard.emptyPath(rock, squareNotHidden));
    }
}
