package piece.extra;

import board.ChessBoard;
import card.QuadrilleCard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Rock;
import piece.extra.BlackHole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BlackHoleTest {

    @Test
    void should_not_be_movable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, "e4");

        assertFalse(chessBoard.tryToMove(blackHole, "e5"));
    }

    @Test
    void should_be_impregnable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        String e4 = "e4";
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, "e1");
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertFalse(chessBoard.tryToMove(rock, e4));
        assertEquals(blackHole, chessBoard.at(e4).getPiece().get());
    }

    @Test
    void should_not_be_jumpable() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        String e4 = "e4";
        Rock rock = new Rock(Color.WHITE);
        chessBoard.add(rock, "e1");
        BlackHole blackHole = new BlackHole();
        chessBoard.add(blackHole, e4);

        assertFalse(chessBoard.tryToMove(rock, "e8"));
        assertEquals(blackHole, chessBoard.at(e4).getPiece().get());
    }

    @Test
    void should_not_turn_if_black_hole_in_corner() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        String a1 = "a1";
        chessBoard.removePieceFromTheBoard(chessBoard.at(a1).getPiece().get());
        chessBoard.add(new BlackHole(), a1);

        Assertions.assertThrows(BlackHole.BlackHoleException.class, () -> new QuadrilleCard().playOn(chessBoard, List.of(QuadrilleCard.Direction.CLOCKWISE)));
    }
}