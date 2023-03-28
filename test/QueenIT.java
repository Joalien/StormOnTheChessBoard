import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueenIT {

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Queen queen = new Queen("a1", Color.WHITE);
        chessBoard.add(queen, "a1");
        String squareHidden = "h1";
        String squareHidden_bis = "h8";
        String squareNotHidden = "a8";
        assertTrue(chessBoard.arePiecesOnThePath(queen, squareHidden));
        assertTrue(chessBoard.arePiecesOnThePath(queen, squareHidden_bis));
        assertTrue(chessBoard.arePiecesOnThePath(queen, squareNotHidden));

        chessBoard.add(new Queen("e1", Color.BLACK), "e1");
        chessBoard.add(new Queen("d4", Color.BLACK), "d4");

        assertFalse(chessBoard.arePiecesOnThePath(queen, squareHidden));
        assertFalse(chessBoard.arePiecesOnThePath(queen, squareHidden_bis));
        assertTrue(chessBoard.arePiecesOnThePath(queen, squareNotHidden));
    }
}
