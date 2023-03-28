import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RockIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Rock rock = new Rock("a1", Color.WHITE);
        chessBoard.add(rock, "a1");
        String squareHidden = "h1";
        String squareNotHidden = "a8";
        assertTrue(chessBoard.arePiecesOnThePath(rock, squareHidden));
        assertTrue(chessBoard.arePiecesOnThePath(rock, squareNotHidden));

        chessBoard.add(new Rock("e1", Color.BLACK), "e1");

        assertFalse(chessBoard.arePiecesOnThePath(rock, squareHidden));
        assertTrue(chessBoard.arePiecesOnThePath(rock, squareNotHidden));
    }
}
