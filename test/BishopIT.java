import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BishopIT {

    @Test
    void move_on_empty_chessboard() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        String a1 = "a1";
        String h8 = "h8";
        Bishop bishop = new Bishop(a1, Color.WHITE);
        chessBoard.add(bishop, a1);
        assertEquals(bishop, chessBoard.at(a1).getPiece().get());
        assertTrue(chessBoard.at(h8).getPiece().isEmpty());

        assertTrue(chessBoard.move(bishop, h8));

        assertTrue(chessBoard.at(a1).getPiece().isEmpty());
        assertEquals(bishop, chessBoard.at(h8).getPiece().get());
    }

    @Test
    void should_not_be_able_to_jump_over_pieces() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        Bishop bishop = new Bishop("a1", Color.WHITE);
        chessBoard.add(bishop, "a1");
        String squareToMoveOn = "c3";
        assertTrue(chessBoard.arePiecesOnThePath(bishop, squareToMoveOn));

        chessBoard.add(new Bishop("b2", Color.BLACK), "b2");

        assertFalse(chessBoard.arePiecesOnThePath(bishop, squareToMoveOn));
    }
}
